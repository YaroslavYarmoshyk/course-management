package com.coursemanagement.service;

import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.service.impl.UserAssociationServiceImpl;
import com.coursemanagement.util.AuthorizationUtil;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.coursemanagement.util.TestDataUtils.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Order(1)
    @Test
    @DisplayName("Test admin access")
    void testCurrentUserAccess_Admin() {
        try (final MockedStatic<AuthorizationUtil> mockedUtils = mockStatic(AuthorizationUtil.class)) {
            mockedUtils.when(AuthorizationUtil::isCurrentUserAdmin).thenReturn(true);

            assertTrue(userAssociationService.currentUserHasAccessTo(FIRST_STUDENT.getId()));
            assertTrue(userAssociationService.currentUserHasAccessTo(SECOND_STUDENT.getId()));
        }
    }

    @Order(2)
    @Test
    @DisplayName("Test current user access")
    void testCurrentUserAccess_SameUser() {
        try (final MockedStatic<AuthorizationUtil> mockedUtils = mockStatic(AuthorizationUtil.class)) {
            final Long studentId = FIRST_STUDENT.getId();
            mockedUtils.when(AuthorizationUtil::isCurrentUserAdmin).thenReturn(false);
            when(userService.resolveCurrentUser()).thenReturn(FIRST_STUDENT);

            assertTrue(userAssociationService.currentUserHasAccessTo(studentId));
            assertFalse(userAssociationService.currentUserHasAccessTo(SECOND_STUDENT.getId()));
        }
    }

    //    TODO: Try to use parametrized test
    @Order(3)
    @Test
    @DisplayName("Test admin course association")
    void testUserCourseAssociation_Admin() {
        try (final MockedStatic<AuthorizationUtil> mockedUtils = mockStatic(AuthorizationUtil.class)) {
            final Long studentId = FIRST_STUDENT.getId();
            final Long courseCode = getRandomUserCourseByUser(FIRST_STUDENT).getCourse().getCode();
            mockedUtils.when(AuthorizationUtil::isCurrentUserAdmin).thenReturn(true);

            assertTrue(userAssociationService.isUserAssociatedWithCourse(studentId, courseCode));
            assertTrue(userAssociationService.isUserAssociatedWithCourse(SECOND_STUDENT.getId(), courseCode));
        }
    }

    @Order(4)
    @Test
    @DisplayName("Test user course association")
    void testUserCourseAssociation_User() {
        try (final MockedStatic<AuthorizationUtil> mockedUtils = mockStatic(AuthorizationUtil.class)) {
            final Long studentId = FIRST_STUDENT.getId();
            final Long courseCode = getRandomUserCourseByUser(FIRST_STUDENT).getCourse().getCode();
            mockedUtils.when(AuthorizationUtil::isCurrentUserAdmin).thenReturn(false);
            when(courseRepository.existsByUserCoursesUserIdAndCode(studentId, courseCode)).thenReturn(true);

            assertTrue(userAssociationService.isUserAssociatedWithCourse(studentId, courseCode));
            assertFalse(userAssociationService.isUserAssociatedWithCourse(SECOND_STUDENT.getId(), courseCode));
        }
    }

    @Order(4)
    @Test
    @DisplayName("Test user lesson association")
    void testUserLessonAssociation() {
        try (final MockedStatic<AuthorizationUtil> mockedUtils = mockStatic(AuthorizationUtil.class)) {
            final Long studentId = FIRST_STUDENT.getId();
            final Long lessonId = 1L;
            mockedUtils.when(AuthorizationUtil::isCurrentUserAdmin).thenReturn(false);
            when(lessonRepository.existsByCourseUserCoursesUserIdAndId(studentId, lessonId)).thenReturn(true);

            assertTrue(userAssociationService.isUserAssociatedWithLesson(studentId, lessonId));
            assertFalse(userAssociationService.isUserAssociatedWithLesson(SECOND_STUDENT.getId(), lessonId));
        }
    }

    @Order(5)
    @Test
    @DisplayName("Test user lesson file association")
    void testUserLessonFileAssociation() {
        try (final MockedStatic<AuthorizationUtil> mockedUtils = mockStatic(AuthorizationUtil.class)) {
            final LessonContent lessonContent = Instancio.create(LessonContent.class);
            final Long studentId = FIRST_STUDENT.getId();
            final Long fileId = lessonContent.getFileId();
            final Long lessonId = lessonContent.getLessonId();
            mockedUtils.when(AuthorizationUtil::isCurrentUserAdmin).thenReturn(false);
            when(lessonContentRepository.findByFileId(fileId)).thenReturn(lessonContent);
            when(lessonRepository.existsByCourseUserCoursesUserIdAndId(studentId, lessonId)).thenReturn(true);

            assertTrue(userAssociationService.isUserAssociatedWithLessonFile(studentId, fileId));
            assertFalse(userAssociationService.isUserAssociatedWithLessonFile(SECOND_STUDENT.getId(), fileId));
        }
    }
}