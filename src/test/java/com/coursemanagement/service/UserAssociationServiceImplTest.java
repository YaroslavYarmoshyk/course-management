package com.coursemanagement.service;

import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.service.impl.UserAssociationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.getRandomUserCourseByUser;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class UserAssociationServiceImplTest {
    @InjectMocks
    private UserAssociationServiceImpl userAssociationService;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonContentRepository lessonContentRepository;

    @Test
    @DisplayName("Test user course association")
    void testUserCourseAssociation() {
        final Long studentId = FIRST_STUDENT.getId();
        final Long courseCode = getRandomUserCourseByUser(FIRST_STUDENT).getCourse().getCode();
        when(courseRepository.existsByUserCoursesUserIdAndCode(studentId, courseCode)).thenReturn(true);

        userAssociationService.isUserAssociatedWithCourse(studentId, courseCode);

        verify(courseRepository).existsByUserCoursesUserIdAndCode(studentId, courseCode);
    }
}