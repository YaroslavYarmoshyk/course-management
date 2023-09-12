package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.CourseFeedbackRepository;
import com.coursemanagement.rest.dto.FeedbackRequestDto;
import com.coursemanagement.service.UserAssociationService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import com.coursemanagement.service.impl.FeedbackServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.AuthorizationUtil.userHasAnyRole;
import static com.coursemanagement.util.TestDataUtils.*;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class FeedbackServiceImplTest {
    @InjectMocks
    private FeedbackServiceImpl feedbackService;
    @Mock
    private CourseFeedbackRepository feedbackRepository;
    @Mock
    private UserAssociationService userAssociationService;
    @Mock
    private UserCourseService userCourseService;
    @Mock
    private UserService userService;
    @Spy
    private ModelMapper mapper;

    @TestFactory
    @DisplayName("Test provide feedback to student course")
    Stream<DynamicTest> testFeedbackSubmission() {
        final UserCourse userCourse = getRandomUserCourseByUser(FIRST_STUDENT);
        final Long courseCode = userCourse.getCourse().getCode();
        final FeedbackRequestDto requestDto = new FeedbackRequestDto(
                INSTRUCTOR.getId(),
                FIRST_STUDENT.getId(),
                courseCode,
                "Test feedback"
        );
        final User secondInstructor = Instancio.of(USER_TEST_MODEL)
                .set(field(User::getId), requestDto.instructorId() + 1)
                .set(field(User::getRoles), Set.of(Role.INSTRUCTOR))
                .create();
        when(userService.getUserById(INSTRUCTOR.getId())).thenReturn(INSTRUCTOR);
        return Stream.of(
                dynamicTest("Test current user is neither admin or instructor",
                        () -> testFeedbackValidation(
                                FIRST_STUDENT,
                                requestDto,
                                true,
                                "Current user is not allowed to provide feedback to student course")),
                dynamicTest("Test current user is another instructor",
                        () -> testFeedbackValidation(
                                secondInstructor,
                                requestDto,
                                true,
                                "Current user is not allowed to provide feedback to student course"
                        )),
                dynamicTest("Test student is not associated with course",
                        () -> testFeedbackValidation(
                                INSTRUCTOR,
                                requestDto,
                                false,
                                "Student is not associated with course"
                        ))
        );
    }

    void testFeedbackValidation(final User currentUser,
                                final FeedbackRequestDto feedbackRequestDto,
                                final boolean studentAssociatedWithCourse,
                                final String expectedMessage) {
        final Long instructorId = feedbackRequestDto.instructorId();
        final Long studentId = feedbackRequestDto.studentId();
        final Long courseCode = feedbackRequestDto.courseCode();
        final boolean currentAdmin = userHasAnyRole(currentUser, Role.ADMIN);
        final boolean sameUserWithInstructorRole = Objects.equals(currentUser.getId(), feedbackRequestDto.instructorId()) && userHasAnyRole(currentUser, Role.INSTRUCTOR);
        when(userAssociationService.currentUserHasAccessTo(instructorId)).thenReturn(currentAdmin || sameUserWithInstructorRole);
        when(userAssociationService.isUserAssociatedWithCourse(studentId, courseCode)).thenReturn(studentAssociatedWithCourse);

        assertThrowsWithMessage(
                () -> feedbackService.provideFeedbackToUserCourse(feedbackRequestDto),
                SystemException.class,
                expectedMessage
        );
    }

}