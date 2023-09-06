package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.model.CourseFeedback;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.CourseFeedbackRepository;
import com.coursemanagement.repository.entity.CourseFeedbackEntity;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.rest.dto.FeedbackResponseDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.FeedbackService;
import com.coursemanagement.service.UserAssociationService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.coursemanagement.enumeration.Role.ADMIN;
import static com.coursemanagement.enumeration.Role.INSTRUCTOR;
import static com.coursemanagement.util.AuthorizationUtil.userHasAnyRole;
import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final CourseFeedbackRepository feedbackRepository;
    private final UserAssociationService userAssociationService;
    private final UserCourseService userCourseService;
    private final UserService userService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public FeedbackResponseDto provideFeedbackToUserCourse(final FeedbackRequestDto feedbackRequestDto) {
        final User instructor = userService.getUserById(feedbackRequestDto.instructorId());
        final Long studentId = feedbackRequestDto.studentId();
        final Long courseCode = feedbackRequestDto.courseCode();
        validateFeedbackSubmission(instructor, studentId, courseCode);

        final UserCourse studentCourse = userCourseService.getUserCourse(studentId, courseCode);
        final CourseFeedback courseFeedback = CourseFeedback.builder()
                .withStudentId(studentId)
                .withCourseCode(courseCode)
                .withInstructorId(instructor.getId())
                .withFeedback(feedbackRequestDto.feedback())
                .withFeedbackSubmissionDate(LocalDateTime.now(DEFAULT_ZONE_ID))
                .build();
        final CourseFeedbackEntity savedEntity = feedbackRepository.save(mapper.map(courseFeedback, CourseFeedbackEntity.class));

        return FeedbackResponseDto.builder()
                .withStudent(new UserDto(studentCourse.getUser()))
                .withUserCourse(new UserCourseDto(studentCourse))
                .withInstructor(new UserDto(instructor))
                .withFeedback(savedEntity.getFeedback())
                .withFeedbackSubmissionDate(savedEntity.getFeedbackSubmissionDate())
                .build();
    }

    private void validateFeedbackSubmission(final User instructor, final Long studentId, final Long courseCode) {
        validateInstructor(instructor);
        validateUserCourseAccess(studentId, courseCode);
    }

    private void validateInstructor(final User instructor) {
        final boolean currentUserHasAccess = userAssociationService.currentUserHasAccessTo(instructor.getId());
        final boolean currentUserAdminOrInstructor = userHasAnyRole(instructor, ADMIN, INSTRUCTOR);
        if (!currentUserHasAccess || !currentUserAdminOrInstructor) {
            throw new SystemException("Current user is not allowed to provide feedback to student course", SystemErrorCode.FORBIDDEN);
        }
    }

    private void validateUserCourseAccess(final Long studentId, final Long courseCode) {
        if (!userAssociationService.isUserAssociatedWithCourse(studentId, courseCode)) {
            throw new SystemException("Student is not associated with course", SystemErrorCode.FORBIDDEN);
        }
    }
}
