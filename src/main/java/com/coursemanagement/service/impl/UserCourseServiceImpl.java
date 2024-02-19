package com.coursemanagement.service.impl;

import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.UserCourseRepository;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.rest.dto.CourseFeedbackDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserInfoDto;
import com.coursemanagement.service.FeedbackService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCourseServiceImpl implements UserCourseService {
    private final UserCourseRepository userCourseRepository;
    private final FeedbackService feedbackService;
    private final MarkService markService;
    private final ModelMapper mapper;

    @Override
    public UserCourse getUserCourse(final Long userId, final Long courseCode) {
        return userCourseRepository.findByUserIdAndCourseCode(userId, courseCode)
                .map(userCourseEntity -> mapper.map(userCourseEntity, UserCourse.class))
                .orElseThrow(() -> new SystemException("User is not associated with course", SystemErrorCode.FORBIDDEN));
    }

    @Override
    public Set<UserCourse> getUserCoursesByUserId(final Long userId) {
        final List<UserCourseEntity> userCourseEntities = userCourseRepository.findByUserId(userId);
        return userCourseEntities.stream()
                .map(userCourseEntity -> mapper.map(userCourseEntity, UserCourse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UserCourseDto> getUserCourseSummariesByUserId(final Long userId) {
        return getUserCoursesByUserId(userId).stream()
                .map(UserCourseDto::new)
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
    public List<UserInfoDto> getStudentsByCourseCode(final Long courseCode) {
        return userCourseRepository.findStudentsByCourseCode(courseCode).stream()
                .map(UserCourseEntity::getUser)
                .map(UserInfoDto::new)
                .sorted(Comparator.comparingLong(UserInfoDto::id))
                .toList();
    }

    @Override
    public UserCourseDetailsDto getUserCourseDetails(final Long userId, final Long courseCode) {
        final UserCourse userCourse = getUserCourse(userId, courseCode);
        final CourseMark courseMark = markService.getStudentCourseMark(userId, courseCode);
        final Set<CourseFeedbackDto> feedback = feedbackService.getTotalCourseFeedback(userId, courseCode);
        return new UserCourseDetailsDto(userCourse, courseMark, feedback);
    }
}
