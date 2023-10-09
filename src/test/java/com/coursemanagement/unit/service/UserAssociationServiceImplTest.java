package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.LessonContent;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.service.UserService;
import com.coursemanagement.service.impl.UserAssociationServiceImpl;
import com.coursemanagement.util.AuthorizationUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.stream.Stream;

import static com.coursemanagement.util.AuthorizationUtils.userHasAnyRole;
import static com.coursemanagement.util.TestDataUtils.ADMIN;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.SECOND_STUDENT;
import static com.coursemanagement.util.TestDataUtils.getRandomCourseContainingUser;
import static com.coursemanagement.util.TestDataUtils.getRandomLessonsByCourse;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserAssociationServiceImplTest {
    @InjectMocks
    private UserAssociationServiceImpl userAssociationService;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonContentRepository lessonContentRepository;
    @Mock
    private UserService userService;

    @TestFactory
    @DisplayName("Test user access based on association ")
    Stream<DynamicNode> testUserAssociationAccess() {
        final Course firstStudentCourse = getRandomCourseContainingUser(FIRST_STUDENT);
        final Course secondStudentCourse = getRandomCourseContainingUser(SECOND_STUDENT);
        final Lesson firstStudentLesson = getRandomLessonsByCourse(firstStudentCourse).stream().findFirst().orElse(new Lesson());
        final Lesson secondStudentLesson = getRandomLessonsByCourse(secondStudentCourse).stream().findFirst().orElse(new Lesson());
        final Long fileId = 1L;
        return Stream.of(
                dynamicContainer("Test current user access", Stream.of(
                        dynamicTest("Test current admin access to user", () -> testCurrentUserAccess(ADMIN, FIRST_STUDENT)),
                        dynamicTest("Test current user access", () -> testCurrentUserAccess(FIRST_STUDENT, FIRST_STUDENT)),
                        dynamicTest("Test current user access to another user", () -> testCurrentUserAccess(FIRST_STUDENT, SECOND_STUDENT))
                )),
                dynamicContainer("Test user course access", Stream.of(
                        dynamicTest("Test admin access to a course when the requested user have access",
                                () -> testUserCourseAccess(ADMIN, FIRST_STUDENT, firstStudentCourse)),
                        dynamicTest("Test admin access to a course when the requested user doesn't have access",
                                () -> testUserCourseAccess(ADMIN, FIRST_STUDENT, secondStudentCourse)),
                        dynamicTest("Test current user has access to their course",
                                () -> testUserCourseAccess(FIRST_STUDENT, FIRST_STUDENT, firstStudentCourse)),
                        dynamicTest("Test current user doesn't have access to their course",
                                () -> testUserCourseAccess(FIRST_STUDENT, FIRST_STUDENT, secondStudentCourse)),
                        dynamicTest("Test user access to their course when the current user is different",
                                () -> testUserCourseAccess(FIRST_STUDENT, SECOND_STUDENT, secondStudentCourse))
                )),
                dynamicContainer("Test user lesson access", Stream.of(
                        dynamicTest("Test admin access to a lesson when the requested user have access",
                                () -> testUserLessonAccess(ADMIN, FIRST_STUDENT, firstStudentLesson)),
                        dynamicTest("Test admin access to a lesson when the requested user doesn't have access",
                                () -> testUserLessonAccess(ADMIN, FIRST_STUDENT, secondStudentLesson)),
                        dynamicTest("Test current user has access to their lesson",
                                () -> testUserLessonAccess(FIRST_STUDENT, FIRST_STUDENT, firstStudentLesson)),
                        dynamicTest("Test current user doesn't have access to their lesson",
                                () -> testUserLessonAccess(FIRST_STUDENT, FIRST_STUDENT, secondStudentLesson)),
                        dynamicTest("Test user access to their lesson when the current user is different",
                                () -> testUserLessonAccess(FIRST_STUDENT, SECOND_STUDENT, secondStudentLesson))
                )),
                dynamicContainer("Test user lesson file access", Stream.of(
                        dynamicTest("Test admin access to a lesson file when the requested user have access",
                                () -> testUserLessonFileAccess(ADMIN, FIRST_STUDENT, fileId, true)),
                        dynamicTest("Test admin access to a lesson file when the requested user doesn't have access",
                                () -> testUserLessonFileAccess(ADMIN, FIRST_STUDENT, fileId, false)),
                        dynamicTest("Test current user has access to their lesson file",
                                () -> testUserLessonFileAccess(FIRST_STUDENT, FIRST_STUDENT, fileId, true)),
                        dynamicTest("Test current user doesn't have access to their lesson file",
                                () -> testUserLessonFileAccess(FIRST_STUDENT, FIRST_STUDENT, fileId, false)),
                        dynamicTest("Test user access to their lesson file when the current user is different",
                                () -> testUserLessonFileAccess(FIRST_STUDENT, SECOND_STUDENT, fileId, true))
                )));
    }

    void testCurrentUserAccess(final User currentUser, final User requestedUser) {
        final boolean currentUserAdmin = userHasAnyRole(currentUser, Role.ADMIN);
        final boolean currentUserIsRequestedOne = Objects.equals(currentUser.getId(), requestedUser.getId());
        final boolean hasAccess = currentUserAdmin || currentUserIsRequestedOne;

        try (final MockedStatic<AuthorizationUtils> mockedUtils = mockStatic(AuthorizationUtils.class)) {
            when(userService.resolveCurrentUser()).thenReturn(currentUser);
            mockedUtils.when(AuthorizationUtils::isCurrentUserAdmin).thenReturn(currentUserAdmin);

            assertEquals(hasAccess, userAssociationService.currentUserHasAccessTo(requestedUser.getId()));
        }
    }

    void testUserCourseAccess(final User currentUser, final User requestedUser, final Course course) {
        final boolean hasAccessToCourse = course.getUsers().contains(requestedUser);
        final boolean currentUserAdmin = userHasAnyRole(currentUser, Role.ADMIN);
        final boolean hasAccess = currentUserAdmin || hasAccessToCourse;

        try (final MockedStatic<AuthorizationUtils> mockedUtils = mockStatic(AuthorizationUtils.class)) {
            mockedUtils.when(AuthorizationUtils::isCurrentUserAdmin).thenReturn(currentUserAdmin);
            when(courseRepository.existsByUserCoursesUserIdAndCode(anyLong(), anyLong())).thenReturn(hasAccessToCourse);

            assertEquals(hasAccess, userAssociationService.isUserAssociatedWithCourse(requestedUser.getId(), course.getCode()));
        }
    }

    void testUserLessonAccess(final User currentUser, final User requestedUser, final Lesson lesson) {
        final boolean hasAccessToLesson = lesson.getCourse().getUsers().contains(requestedUser);
        final boolean currentUserAdmin = userHasAnyRole(currentUser, Role.ADMIN);
        final boolean hasAccess = currentUserAdmin || hasAccessToLesson;

        try (final MockedStatic<AuthorizationUtils> mockedUtils = mockStatic(AuthorizationUtils.class)) {
            mockedUtils.when(AuthorizationUtils::isCurrentUserAdmin).thenReturn(currentUserAdmin);
            when(lessonRepository.existsByCourseUserCoursesUserIdAndId(anyLong(), anyLong())).thenReturn(hasAccessToLesson);

            assertEquals(hasAccess, userAssociationService.isUserAssociatedWithLesson(requestedUser.getId(), lesson.getId()));
        }
    }

    void testUserLessonFileAccess(final User currentUser, final User requestedUser, final Long fileId, final boolean hasAccessToLesson) {
        final boolean currentUserAdmin = userHasAnyRole(currentUser, Role.ADMIN);
        final boolean hasAccess = currentUserAdmin || hasAccessToLesson;
        final LessonContent lessonContent = Instancio.of(LessonContent.class)
                .set(field(LessonContent::getFileId), fileId)
                .create();

        try (final MockedStatic<AuthorizationUtils> mockedUtils = mockStatic(AuthorizationUtils.class)) {
            mockedUtils.when(AuthorizationUtils::isCurrentUserAdmin).thenReturn(currentUserAdmin);
            when(lessonContentRepository.findByFileId(fileId)).thenReturn(lessonContent);
            when(userAssociationService.isUserAssociatedWithLesson(requestedUser.getId(), lessonContent.getLessonId())).thenReturn(hasAccessToLesson);

            assertEquals(hasAccess, userAssociationService.isUserAssociatedWithLessonFile(requestedUser.getId(), fileId));
        }
    }
}