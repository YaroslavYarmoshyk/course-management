package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.UserCourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.rest.dto.CourseDetailsDto;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final MarkService markService;
    private final ModelMapper mapper;

    @Override
    public Course getCourseByCode(final Long code) {
        return courseRepository.findByCode(code)
                .map(entity -> mapper.map(entity, Course.class))
                .orElseThrow(() -> new SystemException("Course with code " + code + " not found", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public Set<Course> getCoursesByCodes(final Collection<Long> codes) {
        final Set<CourseEntity> foundCourseEntities = courseRepository.findAllByCodeIn(codes);
        final Set<Long> foundCourseCodes = foundCourseEntities.stream()
                .map(CourseEntity::getCode)
                .collect(Collectors.toSet());

        logMissingCourses(codes, foundCourseCodes);

        return foundCourseEntities.stream()
                .map(entity -> mapper.map(entity, Course.class))
                .collect(Collectors.toSet());
    }

    private static void logMissingCourses(final Collection<Long> codes, final Set<Long> foundCourseCodes) {
        codes.stream()
                .filter(code -> !foundCourseCodes.contains(code))
                .forEach(code -> log.warn("Course by code: {} was not found", code));
    }

    @Override
    public Set<CourseDto> getCoursesByUserId(final Long userId) {
        return getUserCoursesByUserId(userId).stream()
                .map(CourseDto::new)
                .collect(Collectors.toSet());
    }

    @Override
    public UserCourse getUserCourse(final Long userId, final Long courseCode) {
        return userCourseRepository.findByUserIdAndCourseCode(userId, courseCode)
                .map(userCourseEntity -> mapper.map(userCourseEntity, UserCourse.class))
                .orElseThrow(() -> new SystemException("User is not associated with course", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public Set<UserCourse> getUserCoursesByUserId(final Long userId) {
        final List<UserCourseEntity> userCourseEntities = userCourseRepository.findByUserId(userId);
        return userCourseEntities.stream()
                .map(userCourseEntity -> mapper.map(userCourseEntity, UserCourse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public UserCourse saveUserCourse(final UserCourse userCourse) {
        userCourseRepository.save(mapper.map(userCourse, UserCourseEntity.class));
        final Long userId = userCourse.getUser().getId();
        final Long courseCode = userCourse.getCourse().getCode();
        return getUserCourse(userId, courseCode);
    }

    @Override
    public Set<UserDto> getStudentsByCourseCode(final Long courseCode) {
        return userCourseRepository.findStudentsByCourseCode(courseCode).stream()
                .map(UserCourseEntity::getUser)
                .map(UserDto::new)
                .collect(Collectors.toSet());
    }

    @Override
    public void addUserToCourses(final User user, final Collection<Long> courseCodes) {
        final Set<CourseEntity> courseEntities = courseRepository.findAllByCodeIn(courseCodes);
        final UserEntity userEntity = mapper.map(user, UserEntity.class);
        for (final CourseEntity courseEntity : courseEntities) {
            final Set<UserCourseEntity> userCourseEntities = courseEntity.getUserCourses();
            userCourseEntities.add(new UserCourseEntity(userEntity, courseEntity));
        }
        courseRepository.saveAll(courseEntities);
    }

    @Override
    public boolean isUserAssociatedWithCourse(final Long userId, final Long courseCode) {
        return courseRepository.existsByUserCoursesUserIdAndCode(userId, courseCode);
    }

    @Override
    public CourseDetailsDto getCourseDetails(final Long studentId, final Long courseCode) {
        final UserCourse userCourse = getUserCourse(studentId, courseCode);
        if (AuthorizationUtil.isCurrentUserAdminOrInstructor()) {
            return new CourseDetailsDto(userCourse);
        }
        final CourseMark courseMark = markService.getStudentCourseMark(studentId, courseCode);
        return new CourseDetailsDto(userCourse, courseMark);
    }

    @Override
    public CourseMark getStudentCourseFinalMark(final Long studentId, final Long courseCode) {
        validateStudentCourseMarkAccess(studentId, courseCode);
        return markService.getStudentCourseMark(studentId, courseCode);
    }

    private void validateStudentCourseMarkAccess(final Long studentId, final Long courseCode) {
        final UserCourse userCourse = getUserCourse(studentId, courseCode);
        if (!Objects.equals(userCourse.getStatus(), UserCourseStatus.COMPLETED)) {
            throw new SystemException("Cannot get final user course mark. The course has not completed yet", SystemErrorCode.BAD_REQUEST);
        }
    }
}
