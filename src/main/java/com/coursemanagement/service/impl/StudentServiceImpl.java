package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.File;
import com.coursemanagement.model.LessonMark;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.rest.dto.StudentLessonDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.HomeworkService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.StudentService;
import com.coursemanagement.service.UserService;
import com.coursemanagement.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursemanagement.util.Constants.ZERO_MARK_VALUE;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final UserService userService;
    private final CourseService courseService;
    private final LessonService lessonService;
    private final HomeworkService homeworkService;
    private final MarkService markService;
    @Value("${course-management.student.course-limit:5}")
    private int courseLimit;

    @Override
    @Transactional
    public StudentEnrollInCourseResponseDto enrollStudentInCourses(final StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        final Long studentId = studentEnrollInCourseRequestDto.studentId();
        final User student = userService.getUserById(studentId);
        validateStudent(student);

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

    private void validateCourseEnrollment(final Set<Long> requestedCourseCodes,
                                          final Set<Long> alreadyTakenCourseCodes) {
        final Set<Long> requestedNewCourseCodes = requestedCourseCodes.stream()
                .filter(code -> !alreadyTakenCourseCodes.contains(code))
                .collect(Collectors.toSet());
        final int alreadyTakenCoursesCount = alreadyTakenCourseCodes.size();
        final int requestedCoursesCount = requestedNewCourseCodes.size();
        final boolean reachedCourseLimit = courseLimit < (alreadyTakenCoursesCount + requestedCoursesCount);

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

    private void validateStudent(final User potentialStudent) {
        final User currentUser = userService.resolveCurrentUser();
        final boolean isDifferentUser = !Objects.equals(potentialStudent.getId(), currentUser.getId());
        if (isDifferentUser && !AuthorizationUtil.isAdmin(currentUser)) {
            throw new SystemException("Access denied", SystemErrorCode.FORBIDDEN);
        }
        Optional.ofNullable(potentialStudent.getRoles())
                .filter(roles -> roles.contains(Role.STUDENT))
                .orElseThrow(() -> new SystemException("Only students can enroll courses", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public void uploadHomework(final Long studentId, final Long lessonId, final MultipartFile multipartFile) {
        validateHomeworkUploading(studentId, lessonId);
        homeworkService.uploadHomework(studentId, lessonId, multipartFile);
    }

    private void validateHomeworkUploading(final Long studentId, final Long lessonId) {
        if (!AuthorizationUtil.isAdminOrInstructor() && !lessonService.isUserAssociatedWithLesson(studentId, lessonId)) {
            throw new SystemException("Access to the homework uploading is limited to associated lesson only", SystemErrorCode.FORBIDDEN);
        }
    }

    @Override
    public File downloadHomework(final Long studentId, final Long fileId) {
        validateHomeworkDownloading(studentId, fileId);
        return homeworkService.downloadHomework(fileId);
    }

    private void validateHomeworkDownloading(final Long studentId, final Long fileId) {
        if (!AuthorizationUtil.isAdminOrInstructor() && !lessonService.isUserAssociatedWithLessonFile(studentId, fileId)) {
            throw new SystemException("Access to the homework downloading is limited to associated lesson only", SystemErrorCode.FORBIDDEN);
        }
    }

    @Override
    public Set<StudentLessonDto> getStudentLessonsPerCourse(final Long studentId, final Long courseCode) {
        validateUserCourseAccess(studentId, courseCode);
        final Set<LessonDto> lessonsPerCourse = lessonService.getLessonsWithContentPerCourse(studentId, courseCode);
        final Map<Long, Set<LessonMark>> lessonMarks = markService.getStudentLessonMarksByCourseCode(studentId, courseCode)
                .stream()
                .collect(Collectors.groupingBy(LessonMark::getLessonId, Collectors.toSet()));

        return lessonsPerCourse.stream()
                .map(lesson -> getStudentLesson(lesson, lessonMarks.get(lesson.id())))
                .collect(Collectors.toSet());
    }

    private void validateUserCourseAccess(Long userId, Long courseCode) {
        if (!AuthorizationUtil.isAdminOrInstructor() && !courseService.isUserAssociatedWithCourse(userId, courseCode)) {
            throw new SystemException("Access to the lesson is limited to associated students only", SystemErrorCode.FORBIDDEN);
        }
    }

    private static StudentLessonDto getStudentLesson(final LessonDto lesson, final Set<LessonMark> lessonMarks) {
        if (lessonMarks.isEmpty()) {
            return new StudentLessonDto(lesson);
        }
        final double averageMarkValue = lessonMarks.stream()
                .mapToDouble(mark -> mark.getMark().getValue().doubleValue())
                .average()
                .orElse(ZERO_MARK_VALUE);
        final BigDecimal averageMark = BigDecimal.valueOf(averageMarkValue);
        return new StudentLessonDto(lesson, averageMark);
    }
}
