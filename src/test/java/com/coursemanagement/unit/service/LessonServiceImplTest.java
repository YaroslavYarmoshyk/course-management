package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.LessonEntity;
import com.coursemanagement.rest.dto.MarkAssignmentRequestDto;
import com.coursemanagement.rest.dto.UserLessonDto;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserAssociationService;
import com.coursemanagement.service.impl.LessonServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.COURSE_TEST_MODEL;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.INSTRUCTOR;
import static com.coursemanagement.util.TestDataUtils.getRandomCourse;
import static com.coursemanagement.util.TestDataUtils.getRandomLessonsByCourse;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class LessonServiceImplTest {
    @InjectMocks
    @Spy
    private LessonServiceImpl lessonService;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonContentRepository lessonContentRepository;
    @Mock
    private UserAssociationService userAssociationService;
    @Mock
    private MarkService markService;
    @Spy
    private ModelMapper mapper;

    @Order(2)
    @TestFactory
    @DisplayName("Test lesson management")
    Stream<DynamicTest> testGetUserLessonsWithContentPerCourse() {
        final Course course = getRandomCourse();
        final Long userId = FIRST_STUDENT.getId();
        final Long courseCode = course.getCode();

        return Stream.of(
                dynamicTest("Test course access validation whn student doesn't have it", () -> testUserCourseValidation(userId, courseCode)),
                dynamicTest("Test mark assignment validation when current user doesn't have access to instructor",
                        () -> testLessonMarkAssignmentValidation(false, true, "Current user cannot assign mark to lesson")),
                dynamicTest("Test mark assignment validation when student doesn't have access to lesson",
                        () -> testLessonMarkAssignmentValidation(true, false, "Student is not associated with lesson")),
                dynamicTest("Test get lessons by course code", this::testGetLessonsByCourseCode),
                dynamicTest("Test get user lessons with detailed information", () -> testGetUserLessons(userId, courseCode)),
                dynamicTest("Test assign mark to user lesson", this::testLessonMarkAssigment)
        );
    }

    void testUserCourseValidation(final Long userId, final Long courseCode) {
        when(userAssociationService.isUserAssociatedWithCourse(userId, courseCode)).thenReturn(false);

        assertThrowsWithMessage(
                () -> lessonService.getUserLessonsWithContentPerCourse(userId, courseCode),
                SystemException.class,
                "Access to the lesson is limited to associated users only"
        );
    }

    private void testLessonMarkAssignmentValidation(final boolean currentUserHasAccessToInstructor,
                                                    final boolean studentHasAccessToLesson,
                                                    final String expectedMessage) {
        when(userAssociationService.currentUserHasAccessTo(anyLong())).thenReturn(currentUserHasAccessToInstructor);
        when(userAssociationService.isUserAssociatedWithLesson(anyLong(), anyLong())).thenReturn(studentHasAccessToLesson);

        assertThrowsWithMessage(
                () -> lessonService.assignMarkToUserLesson(new MarkAssignmentRequestDto(INSTRUCTOR.getId(), FIRST_STUDENT.getId(), 1L, Mark.EXCELLENT)),
                SystemException.class,
                expectedMessage
        );
    }

    void testGetLessonsByCourseCode() {
        final CourseEntity courseEntity = Instancio.create(CourseEntity.class);
        final Long courseCode = courseEntity.getCode();
        final Set<LessonEntity> lessonEntities = Instancio.ofSet(LessonEntity.class)
                .supply(field(LessonEntity::getCourse), () -> courseEntity)
                .create();

        when(lessonRepository.findAllByCourseCode(courseCode)).thenReturn(lessonEntities);

        final Set<Lesson> foundLessons = lessonService.getLessonsPerCourse(courseCode);
        final Set<Long> expectedLessonIds = lessonEntities.stream()
                .map(LessonEntity::getId)
                .collect(Collectors.toSet());
        final Set<Long> actualLessonIds = foundLessons.stream()
                .map(Lesson::getId)
                .collect(Collectors.toSet());

        assertEquals(lessonEntities.size(), foundLessons.size());
        assertTrue(CollectionUtils.isEqualCollection(expectedLessonIds, actualLessonIds));
    }

    void testGetUserLessons(final Long userId, final Long courseCode) {
        final Course course = Instancio.of(COURSE_TEST_MODEL)
                .set(field(Course::getCode), courseCode)
                .create();
        final Map<Long, Lesson> lessonsPerLessonId = getRandomLessonsByCourse(course).stream()
                .collect(Collectors.toMap(
                        Lesson::getId,
                        Function.identity()
                ));
        final Map<Long, Set<LessonContent>> lessonContentsPerLessonId = lessonsPerLessonId.values().stream()
                .map(lesson -> Instancio.of(LessonContent.class).set(field(LessonContent::getLessonId), lesson.getId()).create())
                .collect(Collectors.groupingBy(
                        LessonContent::getLessonId,
                        Collectors.toSet()
                ));
        final Set<LessonContent> lessonContents = lessonContentsPerLessonId.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        final Map<Long, BigDecimal> lessonMarks = lessonsPerLessonId.values().stream()
                .map(Lesson::getId)
                .collect(Collectors.toMap(
                        id -> id,
                        id -> BigDecimal.valueOf(new Random().nextDouble(1, 5.0))
                ));

        when(userAssociationService.isUserAssociatedWithCourse(userId, courseCode)).thenReturn(true);
        when(lessonService.getLessonsPerCourse(courseCode)).thenReturn(new HashSet<>(lessonsPerLessonId.values()));
        when(lessonContentRepository.findAllByLessonIdIn(anyCollection())).thenReturn(lessonContents);
        when(markService.getAverageLessonMarksForStudentPerCourse(userId, courseCode)).thenReturn(lessonMarks);

        final Set<UserLessonDto> userLessons = lessonService.getUserLessonsWithContentPerCourse(userId, courseCode);

        for (final UserLessonDto userLesson : userLessons) {
            final Long lessonId = userLesson.lessonId();
            final Lesson expectedLesson = lessonsPerLessonId.get(lessonId);
            final BigDecimal expectedMarkValue = lessonMarks.get(lessonId);
            final Mark expectedMark = Mark.of(expectedMarkValue);

            assertTrue(lessonContentsPerLessonId.containsKey(lessonId));
            assertEquals(expectedLesson.getTitle(), userLesson.title());
            assertEquals(expectedLesson.getDescription(), userLesson.description());
            assertEquals(lessonContentsPerLessonId.get(lessonId), userLesson.lessonContent());
            assertEquals(expectedMarkValue, userLesson.markValue());
            assertEquals(expectedMark, userLesson.mark());
        }
    }

    private void testLessonMarkAssigment() {
        final var requestDto = new MarkAssignmentRequestDto(INSTRUCTOR.getId(), FIRST_STUDENT.getId(), 1L, Mark.AVERAGE);

        when(userAssociationService.currentUserHasAccessTo(requestDto.instructorId())).thenReturn(true);
        when(userAssociationService.isUserAssociatedWithLesson(requestDto.studentId(), requestDto.lessonId())).thenReturn(true);

        lessonService.assignMarkToUserLesson(requestDto);
        verify(markService).assignMarkToStudentLesson(requestDto);
    }
}