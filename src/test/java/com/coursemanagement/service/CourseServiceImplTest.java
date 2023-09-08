package com.coursemanagement.service;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.service.impl.CourseServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.getRandomCourses;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class CourseServiceImplTest {
    @Spy
    @InjectMocks
    private CourseServiceImpl courseService;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserCourseService userCourseService;
    @Mock
    private MarkService markService;
    @Spy
    private ModelMapper mapper;


    @Test
    @DisplayName("Test get course by course code")
    void testGetCourseByCode() {
        final CourseEntity courseEntity = Instancio.create(CourseEntity.class);
        final Long courseCode = courseEntity.getCode();
        final Long nonExistingCourseCode = courseCode + 1;
        when(courseRepository.findByCode(courseCode)).thenReturn(Optional.of(courseEntity));
        when(courseRepository.findByCode(nonExistingCourseCode)).thenReturn(Optional.empty());

        final Course foundCourse = courseService.getCourseByCode(courseCode);

        assertEquals(courseCode, foundCourse.getCode());
        assertThrowsWithMessage(
                () -> courseService.getCourseByCode(nonExistingCourseCode),
                SystemException.class,
                "Course with code " + nonExistingCourseCode + " not found"
        );
    }

    @Test
    @DisplayName("Test get courses by codes")
    void testGetCoursesByCodes() {
        final Set<CourseEntity> courseEntities = getRandomCourses().stream()
                .map(course -> mapper.map(course, CourseEntity.class))
                .collect(Collectors.toSet());
        final Set<Long> courseCodes = courseEntities.stream()
                .map(CourseEntity::getCode)
                .collect(Collectors.toSet());
        when(courseRepository.findAllByCodeIn(courseCodes)).thenReturn(courseEntities);

        final Set<Course> foundCourses = courseService.getCoursesByCodes(courseCodes);
        final Set<Long> foundCourseCodes = foundCourses.stream()
                .map(Course::getCode)
                .collect(Collectors.toSet());

        assertTrue(CollectionUtils.isEqualCollection(courseCodes, foundCourseCodes));

    }
}