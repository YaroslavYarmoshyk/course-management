package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserCourseService userCourseService;
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
        return courseRepository.findAllByCodeIn(codes).stream()
                .map(courseEntity -> mapper.map(courseEntity, Course.class))
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
    public CourseMark getStudentCourseFinalMark(final Long studentId, final Long courseCode) {
        validateStudentCourseMarkAccess(studentId, courseCode);
        return markService.getStudentCourseMark(studentId, courseCode);
    }

    private void validateStudentCourseMarkAccess(final Long studentId, final Long courseCode) {
        final UserCourse userCourse = userCourseService.getUserCourse(studentId, courseCode);
        if (!Objects.equals(userCourse.getStatus(), UserCourseStatus.COMPLETED)) {
            throw new SystemException("Cannot get final user course mark. The course has not completed yet", SystemErrorCode.BAD_REQUEST);
        }
    }
}
