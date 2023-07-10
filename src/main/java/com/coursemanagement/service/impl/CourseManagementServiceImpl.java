package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.CourseDetailsDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseManagementService;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserService;
import com.coursemanagement.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class CourseManagementServiceImpl implements CourseManagementService {
    private final UserService userService;
    private final CourseService courseService;
    private final LessonService lessonService;
    private final MarkService markService;
    @Value("${course-management.student.course-limit:5}")
    private int studentCourseLimit;


    @Override
    @Transactional
    public CourseAssignmentResponseDto assignInstructorToCourse(final Long instructorId, final Long courseCode) {
        final User potentialInstructor = userService.getUserById(instructorId);
        validateInstructorAssigment(potentialInstructor);

        courseService.addUserToCourses(potentialInstructor, Set.of(courseCode));

        final Course course = courseService.getCourseByCode(courseCode);
        final Map<Role, Set<UserDto>> usersByRole = getGroupedUsersByRole(course);
        return new CourseAssignmentResponseDto(
                course.getCode(),
                course.getSubject(),
                usersByRole.getOrDefault(Role.INSTRUCTOR, Set.of()),
                usersByRole.getOrDefault(Role.STUDENT, Set.of())
        );
    }

    private static void validateInstructorAssigment(final User potentialInstructor) {
        if (!AuthorizationUtil.isInstructor(potentialInstructor)) {
            throw new SystemException("Cannot assign user to the course, the user is not an instructor", SystemErrorCode.BAD_REQUEST);
        }
    }


    private Map<Role, Set<UserDto>> getGroupedUsersByRole(final Course course) {
        return course.getUsers().stream()
                .flatMap(user -> user.getRoles().stream().map(
                        role -> new AbstractMap.SimpleEntry<>(role, new UserDto(user)))
                )
                .collect(
                        Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                                Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toSet()))
                );
    }

    @Override
    @Transactional
    public StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        final Long studentId = studentEnrollInCourseRequestDto.studentId();
        final User student = userService.getUserById(studentId);
        validateStudentEnrollment(student);

        final Set<UserCourse> alreadyTakenUserCourses = courseService.getUserCoursesByUserId(studentId).stream()
                .filter(userCourse -> Objects.equals(userCourse.getStatus(), UserCourseStatus.STARTED))
                .collect(Collectors.toSet());
        final Set<Long> alreadyTakenCourseCodes = alreadyTakenUserCourses.stream()
                .map(UserCourse::getCourse)
                .map(Course::getCode)
                .collect(Collectors.toSet());
        final Set<Long> requestedCourseCodes = studentEnrollInCourseRequestDto.courseCodes();
        validateCourseEnrollment(requestedCourseCodes, alreadyTakenCourseCodes);

        courseService.addUserToCourses(student, requestedCourseCodes);

        final Set<UserCourse> updatedUserCourses = courseService.getUserCoursesByUserId(studentId);
        final Set<CourseDto> studentCourses = updatedUserCourses.stream()
                .map(CourseDto::new)
                .collect(Collectors.toSet());
        return new StudentEnrollInCourseResponseDto(student.getId(), studentCourses);
    }

    private void validateStudentEnrollment(final User potentialStudent) {
        final User currentUser = userService.resolveCurrentUser();
        final boolean isDifferentUser = !Objects.equals(potentialStudent.getId(), currentUser.getId());
        if (isDifferentUser && !AuthorizationUtil.isAdmin(currentUser)) {
            throw new SystemException("Access denied", SystemErrorCode.FORBIDDEN);
        }
        Optional.ofNullable(potentialStudent.getRoles())
                .filter(roles -> roles.contains(Role.STUDENT))
                .orElseThrow(() -> new SystemException("Only students can enroll courses", SystemErrorCode.BAD_REQUEST));
    }

    private void validateCourseEnrollment(final Set<Long> requestedCourseCodes,
                                          final Set<Long> alreadyTakenCourseCodes) {
        final Set<Long> requestedNewCourseCodes = requestedCourseCodes.stream()
                .filter(code -> !alreadyTakenCourseCodes.contains(code))
                .collect(Collectors.toSet());
        final int alreadyTakenCoursesCount = alreadyTakenCourseCodes.size();
        final int requestedCoursesCount = requestedNewCourseCodes.size();
        final boolean reachedCourseLimit = studentCourseLimit < (alreadyTakenCoursesCount + requestedCoursesCount);

        if (reachedCourseLimit) {
            final String exceptionMessage = String.format(
                    "Course enrollment limit reached for student. " +
                            "Student is already enrolled in %d courses and cannot enroll in %d additional",
                    alreadyTakenCoursesCount,
                    requestedCoursesCount
            );
            throw new SystemException(exceptionMessage, SystemErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public CourseDetailsDto completeStudentCourse(final Long studentId, final Long courseCode) {
        final UserCourse studentCourse = courseService.getUserCourse(studentId, courseCode);
        final Set<Lesson> lessonsPerCourse = lessonService.getLessonsPerCourse(courseCode);
        final Map<Long, BigDecimal> averageLessonMarks = markService.getAverageLessonMarksForStudentPerCourse(studentId, courseCode);

        validateStudentCourseCompletion(lessonsPerCourse, averageLessonMarks);

        studentCourse.setStatus(UserCourseStatus.COMPLETED);
        studentCourse.setAccomplishmentDate(LocalDateTime.now(DEFAULT_ZONE_ID));
        final UserCourse completedStudentCourse = courseService.saveUserCourse(studentCourse);
        final CourseMark courseMark = markService.getStudentCourseMark(studentId, courseCode);
        return new CourseDetailsDto(completedStudentCourse, courseMark);
    }

    private static void validateStudentCourseCompletion(final Set<Lesson> lessonsPerCourse,
                                                        final Map<Long, BigDecimal> averageLessonMarks) {
        final Set<Long> gradedLessonIds = averageLessonMarks.keySet();
        final boolean areAllLessonsGraded = lessonsPerCourse.stream()
                .map(Lesson::getId)
                .allMatch(gradedLessonIds::contains);
        if (!areAllLessonsGraded) {
            throw new SystemException("Cannot complete course, not all lessons are graded", SystemErrorCode.BAD_REQUEST);
        }
    }
}
