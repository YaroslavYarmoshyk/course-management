package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.UserCourseRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.rest.dto.CourseFeedbackDto;
import com.coursemanagement.rest.dto.MarkInfoDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserInfoDto;
import com.coursemanagement.service.FeedbackService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.impl.UserCourseServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.getRandomCourseContainingUser;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(value = {
        MockitoExtension.class,
        InstancioExtension.class
})
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UserCourseServiceImplTest {
    @InjectMocks
    @Spy
    private UserCourseServiceImpl userCourseService;
    @Mock
    private UserCourseRepository userCourseRepository;
    @Mock
    private FeedbackService feedbackService;
    @Mock
    private MarkService markService;
    @Spy
    private ModelMapper mapper;

    @Order(1)
    @TestFactory
    @DisplayName("Test get user courses")
    Stream<DynamicTest> testGetUserCourse() {
        final Course course = getRandomCourseContainingUser(FIRST_STUDENT);
        final Long userId = FIRST_STUDENT.getId();
        final Long courseCode = course.getCode();
        final UserCourseEntity userCourseEntity = Instancio.of(UserCourseEntity.class)
                .set(field(UserEntity::getId), userId)
                .set(field(CourseEntity::getCode), courseCode)
                .create();
        final Set<UserCourseEntity> userCourseEntities = getUserCourseEntitiesForFirstStudent();
        when(userCourseRepository.findByUserIdAndCourseCode(userId, courseCode)).thenReturn(Optional.of(userCourseEntity));
        when(userCourseRepository.findByUserId(userId)).thenReturn(new ArrayList<>(userCourseEntities));

        return Stream.of(
                dynamicTest("Test get user course by user and course", () -> testGetUserCourse(userId, courseCode, userCourseEntity)),
                dynamicTest("Test get user courses by user", () -> testGetUserCoursesByUserId(userId, userCourseEntities)),
                dynamicTest("Test get user course summaries by user", () -> testGetUserCourseSummaries(userId, userCourseEntities)),
                dynamicTest("Test get students by course", () -> testGetStudentsByCourseCode(courseCode)),
                dynamicTest("Test user course details", () -> testGetUserCourseDetails(userId, courseCode, userCourseEntity))
        );
    }

    @Order(2)
    @Test
    @DisplayName("Test save user course")
    void testSaveUserCourse() {
        final UserCourse userCourse = Instancio.create(UserCourse.class);

        doReturn(userCourse).when(userCourseService).getUserCourse(userCourse.getUser().getId(), userCourse.getCourse().getCode());

        userCourseService.saveUserCourse(userCourse);

        verify(userCourseRepository).save(any(UserCourseEntity.class));
    }

    private void testGetUserCourse(final Long userId, final Long courseCode, final UserCourseEntity userCourseEntity) {
        final Long nonExistingCourse = courseCode + 1;
        final UserCourse userCourse = userCourseService.getUserCourse(userId, courseCode);

        assertEquals(userId, userCourse.getUser().getId());
        assertEquals(courseCode, userCourse.getCourse().getCode());
        assertEquals(userCourseEntity.getStatus(), userCourse.getStatus());
        assertThrowsWithMessage(
                () -> userCourseService.getUserCourse(userId, nonExistingCourse),
                SystemException.class,
                "User is not associated with course"
        );
    }

    private void testGetUserCoursesByUserId(final Long userId, final Set<UserCourseEntity> userCourseEntities) {
        when(userCourseRepository.findByUserId(userId)).thenReturn(new ArrayList<>(userCourseEntities));
        final Set<UserCourse> foundUserCourses = userCourseService.getUserCoursesByUserId(userId);
        final Long expectedUserId = FIRST_STUDENT.getId();
        final Set<Long> actualUserIds = foundUserCourses.stream()
                .map(UserCourse::getUser)
                .map(User::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualUserIds.size());
        assertEquals(expectedUserId, actualUserIds.stream().findFirst().orElseThrow());

        final Set<Long> expectedCourseCodes = userCourseEntities.stream()
                .map(UserCourseEntity::getCourse)
                .map(CourseEntity::getCode)
                .collect(Collectors.toSet());
        final Set<Long> actualCourseCodes = foundUserCourses.stream()
                .map(UserCourse::getCourse)
                .map(Course::getCode)
                .collect(Collectors.toSet());

        assertTrue(CollectionUtils.isEqualCollection(expectedCourseCodes, actualCourseCodes));
    }

    private void testGetUserCourseSummaries(final Long userId, final Set<UserCourseEntity> userCourseEntities) {
        final Set<Long> expectedCourseCodes = userCourseEntities.stream()
                .map(UserCourseEntity::getCourse)
                .map(CourseEntity::getCode)
                .collect(Collectors.toSet());

        final Set<UserCourseDto> foundUserCourseSummaries = userCourseService.getUserCourseSummariesByUserId(userId);

        final Set<Long> foundCourseCodes = foundUserCourseSummaries.stream()
                .map(UserCourseDto::code)
                .collect(Collectors.toSet());

        assertTrue(CollectionUtils.isEqualCollection(expectedCourseCodes, foundCourseCodes));
    }

    private void testGetStudentsByCourseCode(final Long courseCode) {
        final Set<UserCourseEntity> studentCourseEntities = Instancio.ofSet(UserCourseEntity.class)
                .set(field(UserEntity::getRoles), Set.of(Role.STUDENT))
                .create();
        final Set<Long> expectedStudentIds = studentCourseEntities.stream()
                .map(UserCourseEntity::getUser)
                .map(UserEntity::getId)
                .collect(Collectors.toSet());

        when(userCourseRepository.findStudentsByCourseCode(courseCode)).thenReturn(new ArrayList<>(studentCourseEntities));

        final Set<Long> foundStudentIds = userCourseService.getStudentsByCourseCode(courseCode).stream()
                .map(UserInfoDto::id)
                .collect(Collectors.toSet());

        assertTrue(CollectionUtils.isEqualCollection(expectedStudentIds, foundStudentIds));
    }

    private void testGetUserCourseDetails(final Long userId, final Long courseCode, final UserCourseEntity userCourseEntity) {
        final CourseMark courseMark = Instancio.of(CourseMark.class)
                .set(field(CourseMark::getStudentId), userId)
                .set(field(CourseMark::getCourseCode), courseCode)
                .create();
        final Set<CourseFeedbackDto> feedback = Instancio.ofSet(CourseFeedbackDto.class).create();
        final CourseEntity courseEntity = userCourseEntity.getCourse();

        when(markService.getStudentCourseMark(userId, courseCode)).thenReturn(courseMark);
        when(feedbackService.getTotalCourseFeedback(userId, courseCode)).thenReturn(feedback);

        final UserCourseDetailsDto userCourseDetails = userCourseService.getUserCourseDetails(userId, courseCode);
        final MarkInfoDto expectedMarkInfo = new MarkInfoDto(courseMark.getLessonMarks(), courseMark.getMarkValue(), courseMark.getMark());
        assertEquals(courseCode, userCourseDetails.courseCode());
        assertEquals(courseEntity.getSubject(), userCourseDetails.subject());
        assertEquals(courseEntity.getDescription(), userCourseDetails.description());
        assertEquals(userCourseEntity.getStatus(), userCourseDetails.status());
        assertEquals(expectedMarkInfo, userCourseDetails.markInfo());
        assertEquals(feedback, userCourseDetails.courseFeedback());
    }

    private Set<UserCourseEntity> getUserCourseEntitiesForFirstStudent() {
        return Instancio.ofSet(UserCourseEntity.class)
                .set(all(UserEntity.class), mapper.map(FIRST_STUDENT, UserEntity.class))
                .create();
    }
}