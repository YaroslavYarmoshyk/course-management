package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.impl.CourseServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Captor
    private ArgumentCaptor<Set<CourseEntity>> courseEntitiesCaptor;

    @Order(1)
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

    @Order(2)
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

    @Order(3)
    @Test
    @DisplayName("Test add user to courses")
    void testAddUserToCourses() {
        final User user = NEW_USER;
        final Set<CourseEntity> courseEntities = generateNewCourses(5);
        final Set<Long> courseCodes = courseEntities.stream()
                .map(CourseEntity::getCode)
                .collect(Collectors.toSet());

        when(courseRepository.findAllByCodeIn(courseCodes)).thenReturn(courseEntities);

        courseService.addUserToCourses(user, courseCodes);
        verify(courseRepository).saveAll(courseEntitiesCaptor.capture());
        final Set<Long> allUsersFromRequestedCourses = courseEntitiesCaptor.getValue().stream()
                .map(CourseEntity::getUserCourses)
                .flatMap(Collection::stream)
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getId)
                .collect(Collectors.toSet());

        assertTrue(allUsersFromRequestedCourses.contains(user.getId()));
    }

    @Order(4)
    @ParameterizedTest(name = "{index} user course status = {0}")
    @EnumSource(value = UserCourseStatus.class)
    @DisplayName("Test get course final mark")
    void testGetCourseFinalMark(final UserCourseStatus userCourseStatus) {
        final UserCourse userCourse = getRandomUserCourseByUser(FIRST_STUDENT);
        final Long studentId = FIRST_STUDENT.getId();
        final Long courseCode = userCourse.getCourse().getCode();
        userCourse.setStatus(userCourseStatus);

        when(userCourseService.getUserCourse(studentId, courseCode)).thenReturn(userCourse);

        switch (userCourseStatus) {
            case COMPLETED -> {
                courseService.getStudentCourseFinalMark(studentId, courseCode);
                verify(markService, atLeastOnce()).getStudentCourseMark(studentId, courseCode);
            }
            case STARTED -> assertThrowsWithMessage(
                    () -> courseService.getStudentCourseFinalMark(studentId, courseCode),
                    SystemException.class,
                    "Cannot get final user course mark. The course has not completed yet"
            );
            default -> fail();
        }
    }
}