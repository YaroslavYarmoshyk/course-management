package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.UserCourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final ModelMapper mapper;

    @Override
    public Course getCourseByCode(final Long code) {
        return courseRepository.findByCode(code)
                .map(entity -> mapper.map(entity, Course.class))
                .orElseThrow(() -> new SystemException("Course with code " + code + " not found", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public Set<UserCourse> getUserCoursesByUserId(final Long userId) {
        final List<UserCourseEntity> userCourseEntities = userCourseRepository.findByUserId(userId);
        return userCourseEntities.stream()
                .map(userCourseEntity -> mapper.map(userCourseEntity, UserCourse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CourseDto> getCoursesByUserId(final Long userId) {
        return getUserCoursesByUserId(userId).stream()
                .map(CourseDto::new)
                .collect(Collectors.toSet());
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
            reEnrollFinishedCourses(userCourseEntities);
        }
        courseRepository.saveAll(courseEntities);
    }

    @Override
    public boolean isUserAssociatedWithCourse(final Long userId, final Long courseCode) {
        return courseRepository.existsByUserCoursesUserIdAndCode(userId, courseCode);
    }

    private static void reEnrollFinishedCourses(final Set<UserCourseEntity> userCourseEntities) {
        for (final UserCourseEntity userCourseEntity : userCourseEntities) {
            final UserCourseStatus status = userCourseEntity.getStatus();
            if (Objects.equals(status, UserCourseStatus.FINISHED)) {
                userCourseEntity.setStatus(UserCourseStatus.STARTED);
                userCourseEntity.setEnrollmentDate(LocalDateTime.now(DEFAULT_ZONE_ID));
                userCourseEntity.setAccomplishmentDate(null);
            }
        }
    }
}
