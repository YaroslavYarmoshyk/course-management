package com.coursemanagement.service;

import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.service.impl.UserAssociationServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.getRandomUserCourseByUser;
import static org.mockito.Mockito.verify;
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

    @Order(1)
    @Test
    @DisplayName("Test user course association")
    void testUserCourseAssociation() {
        final Long studentId = FIRST_STUDENT.getId();
        final Long courseCode = getRandomUserCourseByUser(FIRST_STUDENT).getCourse().getCode();
        when(courseRepository.existsByUserCoursesUserIdAndCode(studentId, courseCode)).thenReturn(true);

        userAssociationService.isUserAssociatedWithCourse(studentId, courseCode);

        verify(courseRepository).existsByUserCoursesUserIdAndCode(studentId, courseCode);
    }

    @Order(2)
    @Test
    @DisplayName("Test user lesson association")
    void testUserLessonAssociation() {
        final Long studentId = FIRST_STUDENT.getId();
        final Long lessonId = 1L;
        when(lessonRepository.existsByCourseUserCoursesUserIdAndId(studentId, lessonId)).thenReturn(true);

        userAssociationService.isUserAssociatedWithLesson(studentId, lessonId);

        verify(lessonRepository).existsByCourseUserCoursesUserIdAndId(studentId, lessonId);
    }

    @Order(3)
    @Test
    @DisplayName("Test user lesson file association")
    void testUserLessonFileAssociation() {
        final LessonContent lessonContent = Instancio.create(LessonContent.class);
        final Long studentId = FIRST_STUDENT.getId();
        final Long fileId = lessonContent.getFileId();
        final Long lessonId = lessonContent.getLessonId();
        when(lessonContentRepository.findByFileId(fileId)).thenReturn(lessonContent);
        when(lessonRepository.existsByCourseUserCoursesUserIdAndId(studentId, lessonId)).thenReturn(true);

        userAssociationService.isUserAssociatedWithLessonFile(studentId, fileId);

        verify(lessonRepository).existsByCourseUserCoursesUserIdAndId(studentId, lessonId);
    }
}