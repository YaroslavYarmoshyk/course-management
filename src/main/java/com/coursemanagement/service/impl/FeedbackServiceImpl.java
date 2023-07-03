package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.CourseFeedback;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.CourseFeedbackRepository;
import com.coursemanagement.repository.entity.CourseFeedbackEntity;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final CourseFeedbackRepository feedbackRepository;
    private final CourseService courseService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public FeedbackResponseDto provideFeedbackToUserCourse(final User instructor,
                                                           final FeedbackRequestDto feedbackRequestDto) {
        final Long studentId = feedbackRequestDto.studentId();
        final Long courseCode = feedbackRequestDto.courseCode();
        final UserCourse studentCourse = courseService.getUserCoursesByUserId(studentId).stream()
                .filter(userCourse -> Objects.equals(userCourse.getStatus(), UserCourseStatus.STARTED))
                .filter(userCourse -> Objects.equals(userCourse.getCourse().getCode(), courseCode))
                .findFirst()
                .orElseThrow(() -> new SystemException("Cannot find student course", SystemErrorCode.BAD_REQUEST));

        final CourseFeedback courseFeedback = CourseFeedback.builder()
                .withStudentId(studentId)
                .withCourseCode(courseCode)
                .withInstructorId(instructor.getId())
                .withFeedback(feedbackRequestDto.feedback())
                .withFeedbackDate(LocalDateTime.now(DEFAULT_ZONE_ID))
                .build();
        final CourseFeedbackEntity savedEntity = feedbackRepository.save(mapper.map(courseFeedback, CourseFeedbackEntity.class));

        return FeedbackResponseDto.builder()
                .withStudent(new UserDto(studentCourse.getUser()))
                .withCourse(new CourseDto(studentCourse))
                .withInstructor(new UserDto(instructor))
                .withFeedback(savedEntity.getFeedback())
                .withFeedbackSubmissionDate(savedEntity.getFeedbackSubmissionDate())
                .build();
    }
}
