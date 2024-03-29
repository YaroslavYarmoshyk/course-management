package com.coursemanagement.service.impl;

import com.coursemanagement.config.properties.CourseProperties;
import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import com.coursemanagement.model.*;
import com.coursemanagement.rest.dto.*;
import com.coursemanagement.service.*;
import com.coursemanagement.util.AuthorizationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.coursemanagement.util.Constants.*;
import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class CourseManagementServiceImpl implements CourseManagementService {
    private final UserService userService;
    private final UserAssociationService userAssociationService;
    private final CourseService courseService;
    private final UserCourseService userCourseService;
    private final FeedbackService feedbackService;
    private final LessonService lessonService;
    private final MarkService markService;
    private final CourseProperties courseProperties;


    @Override
    public InstructorAssignmentResponseDto assignInstructorToCourse(final InstructorAssignmentRequestDto instructorAssignmentRequestDto) {
        final Long instructorId = instructorAssignmentRequestDto.instructorId();
        final Long courseCode = instructorAssignmentRequestDto.courseCode();
        final User potentialInstructor = userService.getUserById(instructorId);
        validateInstructorAssigment(potentialInstructor);

        courseService.addUserToCourses(potentialInstructor, Set.of(courseCode));

        final Course course = courseService.getCourseByCode(courseCode);
        final Map<Role, Set<UserInfoDto>> usersByRole = getGroupedUsersByRole(course);
        return new InstructorAssignmentResponseDto(
                course.getCode(),
                course.getSubject(),
                usersByRole.getOrDefault(Role.INSTRUCTOR, Set.of()),
                usersByRole.getOrDefault(Role.STUDENT, Set.of())
        );
    }

    private static void validateInstructorAssigment(final User potentialInstructor) {
        if (!AuthorizationUtils.userHasAnyRole(potentialInstructor, Role.INSTRUCTOR)) {
            throw new SystemException("Cannot assign user to the course, the user is not an instructor", SystemErrorCode.BAD_REQUEST);
        }
    }


    private Map<Role, Set<UserInfoDto>> getGroupedUsersByRole(final Course course) {
        return course.getUsers().stream()
                .flatMap(user -> user.getRoles().stream().map(
                        role -> new AbstractMap.SimpleEntry<>(role, new UserInfoDto(user)))
                )
                .collect(
                        Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                                Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toSet()))
                );
    }

    @Override
    public StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        final Long studentId = studentEnrollInCourseRequestDto.studentId();
        final User student = userService.getUserById(studentId);
        validateStudentEnrollment(student);

        final Set<Long> alreadyTakenCourseCodes = userCourseService.getUserCoursesByUserId(studentId).stream()
                .filter(userCourse -> Objects.equals(userCourse.getStatus(), UserCourseStatus.STARTED))
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
                .orElseThrow(() -> new SystemException("Only students can enroll courses", SystemErrorCode.FORBIDDEN));
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
        final Set<CourseFeedbackDto> feedback = feedbackService.getTotalCourseFeedback(studentId, courseCode);
        return UserCourseDetailsDto.of(completedStudentCourse, courseMark, feedback);
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
