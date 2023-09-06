package com.coursemanagement.service.impl;

import com.coursemanagement.config.properties.CourseProperties;
import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.CourseAssignmentRequestDto;
import com.coursemanagement.rest.dto.CourseAssignmentResponseDto;
import com.coursemanagement.rest.dto.CourseCompletionRequestDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseManagementService;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserAssociationService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import com.coursemanagement.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
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

import static com.coursemanagement.util.Constants.HUNDRED;
import static com.coursemanagement.util.Constants.MARK_ROUNDING_MODE;
import static com.coursemanagement.util.Constants.MARK_ROUNDING_SCALE;
import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class CourseManagementServiceImpl implements CourseManagementService {
    private final UserService userService;
    private final UserAssociationService userAssociationService;
    private final CourseService courseService;
    private final UserCourseService userCourseService;
    private final LessonService lessonService;
    private final MarkService markService;
    private final CourseProperties courseProperties;


    @Override
    @Transactional
    public CourseAssignmentResponseDto assignInstructorToCourse(final CourseAssignmentRequestDto courseAssignmentRequestDto) {
        final Long instructorId = courseAssignmentRequestDto.instructorId();
        final Long courseCode = courseAssignmentRequestDto.courseCode();
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
        if (!AuthorizationUtil.userHasAnyRole(potentialInstructor, Role.INSTRUCTOR)) {
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

        final Set<UserCourse> alreadyTakenUserCourses = userCourseService.getUserCoursesByUserId(studentId).stream()
                .filter(userCourse -> Objects.equals(userCourse.getStatus(), UserCourseStatus.STARTED))
                .collect(Collectors.toSet());
        final Set<Long> alreadyTakenCourseCodes = alreadyTakenUserCourses.stream()
                .map(UserCourse::getCourse)
                .map(Course::getCode)
                .collect(Collectors.toSet());
        final Set<Long> requestedCourseCodes = studentEnrollInCourseRequestDto.courseCodes();
        final Set<Long> foundRequestedCourseCodes = courseService.getCoursesByCodes(requestedCourseCodes).stream()
                .map(Course::getCode)
                .collect(Collectors.toSet());
        validateCourseEnrollment(foundRequestedCourseCodes, alreadyTakenCourseCodes);

        courseService.addUserToCourses(student, foundRequestedCourseCodes);

        final Set<UserCourse> updatedUserCourses = userCourseService.getUserCoursesByUserId(studentId);
        final Set<UserCourseDto> studentCourses = updatedUserCourses.stream()
                .map(UserCourseDto::new)
                .collect(Collectors.toSet());
        return new StudentEnrollInCourseResponseDto(student.getId(), studentCourses);
    }

    private void validateStudentEnrollment(final User potentialStudent) {
        if (!userAssociationService.currentUserHasAccessTo(potentialStudent.getId())) {
            throw new SystemException("Current user cannot enroll in courses for requested one", SystemErrorCode.FORBIDDEN);
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
        final boolean reachedCourseLimit = courseProperties.getStudentCourseLimit() < (alreadyTakenCoursesCount + requestedCoursesCount);

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
    public UserCourseDetailsDto completeStudentCourse(final CourseCompletionRequestDto courseCompletionRequestDto) {
        final Long studentId = courseCompletionRequestDto.studentId();
        final Long courseCode = courseCompletionRequestDto.courseCode();
        final UserCourse studentCourse = userCourseService.getUserCourse(studentId, courseCode);
        final Set<Lesson> lessonsPerCourse = lessonService.getLessonsPerCourse(courseCode);
        final CourseMark courseMark = markService.getStudentCourseMark(studentId, courseCode);
        validateStudentCourseCompletion(lessonsPerCourse, courseMark);

        studentCourse.setStatus(UserCourseStatus.COMPLETED);
        studentCourse.setAccomplishmentDate(LocalDateTime.now(DEFAULT_ZONE_ID));
        final UserCourse completedStudentCourse = userCourseService.saveUserCourse(studentCourse);
        return new UserCourseDetailsDto(completedStudentCourse, courseMark);
    }

    private void validateStudentCourseCompletion(final Set<Lesson> lessonsPerCourse,
                                                 final CourseMark courseMark) {
        final Map<Long, BigDecimal> averageLessonMarks = courseMark.getLessonMarks();
        final Set<Long> gradedLessonIds = averageLessonMarks.keySet();
        final boolean allLessonsGraded = lessonsPerCourse.stream()
                .map(Lesson::getId)
                .allMatch(gradedLessonIds::contains);
        if (!allLessonsGraded) {
            throw new SystemException("Cannot complete course, not all lessons are graded", SystemErrorCode.BAD_REQUEST);
        }

        validateCoursePassingPercentage(courseMark);
    }

    private void validateCoursePassingPercentage(final CourseMark courseMark) {
        final BigDecimal minPassingPercentage = courseProperties.getCoursePassingPercentage();
        final BigDecimal actualPercentageOfMaxGrade = courseMark.getMarkValue().divide(Mark.EXCELLENT.getValue(), MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE).multiply(HUNDRED);
        final boolean isBelowPassingPercentage = actualPercentageOfMaxGrade.compareTo(minPassingPercentage) < BigDecimal.ZERO.intValue();
        if (isBelowPassingPercentage) {
            throw new SystemException("Cannot complete course, minimum passing percentage is: " + minPassingPercentage
                    + " but student has: " + actualPercentageOfMaxGrade, SystemErrorCode.BAD_REQUEST
            );
        }
    }
}
