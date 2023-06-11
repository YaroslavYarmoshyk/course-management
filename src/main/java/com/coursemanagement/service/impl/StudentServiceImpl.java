package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.StudentService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final CourseService courseService;
    private final UserCourseService userCourseService;
    private final UserService userService;
    @Value("${course-management.student.course-limit:5}")
    private int courseLimit;

    @Override
    @Transactional
    public StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        final Long studentId = studentEnrollInCourseRequestDto.studentId();
        final User student = userService.getById(studentId);
        final Set<Course> requestedCourses = courseService.getAllByCodes(studentEnrollInCourseRequestDto.courseCodes());
        final Set<Course> alreadyTakenCourses = courseService.getAllActiveByUserId(student.getId());
        validateCourseEnrollment(student, requestedCourses, alreadyTakenCourses);

        courseService.addUserToCourses(student, requestedCourses);
        final Set<UserCourse> userCourses = userCourseService.getAllByUserId(studentId);

        final Set<CourseDto> studentCourses = userCourses.stream()
                .map(CourseDto::new)
                .collect(Collectors.toSet());
        return new StudentEnrollInCourseResponseDto(student.getId(), studentCourses);
    }

    @Override
    public Set<CourseDto> getAllCourses() {
        final User user = userService.resolveCurrentUser();
        return courseService.getAllByUserId(user.getId());
    }

    private void validateCourseEnrollment(final User student, final Set<Course> requestedCourses, final Set<Course> alreadyTakenCourses) {
        validateStudentRole(student);
        final Set<Long> alreadyTakenCourseCodes = alreadyTakenCourses.stream()
                .map(Course::getCode)
                .collect(Collectors.toSet());
        final Set<Long> requestedNewCourseCodes = requestedCourses.stream()
                .map(Course::getCode)
                .filter(code -> !alreadyTakenCourseCodes.contains(code))
                .collect(Collectors.toSet());
        final int alreadyTakenCoursesCount = alreadyTakenCourseCodes.size();
        final int requestedCoursesCount = requestedNewCourseCodes.size();
        final boolean reachedCourseLimit = courseLimit < (alreadyTakenCoursesCount + requestedCoursesCount);

        if (reachedCourseLimit) {
            final String exceptionMessage = String.format(
                    "Course enrollment limit reached for student with userId: %d. "
                            + "Student is already enrolled in %d courses and cannot enroll in %d additional",
                    student.getId(),
                    alreadyTakenCoursesCount,
                    requestedCoursesCount
            );
            throw new SystemException(exceptionMessage, SystemErrorCode.BAD_REQUEST);
        }
    }

    private static void validateStudentRole(final User potentialStudent) {
        Optional.ofNullable(potentialStudent.getRoles())
                .filter(roles -> roles.contains(Role.STUDENT))
                .orElseThrow(() -> new SystemException("Only students can enroll courses", SystemErrorCode.BAD_REQUEST));
    }
}
