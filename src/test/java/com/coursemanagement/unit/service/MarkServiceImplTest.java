package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.repository.LessonMarkRepository;
import com.coursemanagement.repository.entity.LessonEntity;
import com.coursemanagement.repository.entity.LessonMarkEntity;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.service.impl.MarkServiceImpl;
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

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.coursemanagement.util.Constants.MARK_ROUNDING_MODE;
import static com.coursemanagement.util.Constants.MARK_ROUNDING_SCALE;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(value = {
        MockitoExtension.class,
        InstancioExtension.class
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MarkServiceImplTest {
    @InjectMocks
    @Spy
    private MarkServiceImpl markService;
    @Mock
    private LessonMarkRepository lessonMarkRepository;
    @Spy
    private ModelMapper mapper;

    @Order(1)
    @TestFactory
    @DisplayName("Test getting marks")
    Stream<DynamicTest> testGetMarks() {
        final Long studentId = FIRST_STUDENT.getId();
        final Long courseCode = 103L;
        final Set<LessonMarkEntity> lessonMarkEntities = getLessonMarkEntities();
        final Map<Long, BigDecimal> expectedMarks = Map.of(
                1L, BigDecimal.valueOf(3).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE),
                2L, BigDecimal.valueOf(4.5).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE)
        );

        return Stream.of(
                dynamicTest("Test get average lesson marks for student course",
                        () -> testGetAverageLessonMarks(studentId, courseCode, lessonMarkEntities, expectedMarks)),
                dynamicTest("Test get student course mark based on lesson marks",
                        () -> testGetStudentCourseMark(studentId, courseCode, expectedMarks)),
                dynamicTest("Test get student course mark without lesson marks",
                        () -> testGetStudentCourseMark(studentId, courseCode, Map.of()))
        );
    }

    @Order(2)
    @Test
    @DisplayName("Test student mark assignment")
    void testStudentMarkAssignment() {
        final LessonMarkEntity lessonMarkEntity = Instancio.create(LessonMarkEntity.class);
        final Long instructorId = lessonMarkEntity.getInstructor().getId();
        final Long studentId = lessonMarkEntity.getStudent().getId();
        final Long lessonId = lessonMarkEntity.getLesson().getId();
        final Mark mark = lessonMarkEntity.getMark();
        final var requestDto = new MarkAssigmentRequestDto(instructorId, studentId, lessonId, mark);

        when(lessonMarkRepository.save(argThat(entity -> {
            assertEquals(instructorId, entity.getInstructor().getId());
            assertEquals(studentId, entity.getStudent().getId());
            assertEquals(lessonId, entity.getLesson().getId());
            return true;
        }))).thenReturn(lessonMarkEntity);
        when(lessonMarkRepository.findLessonMarkById(lessonMarkEntity.getId())).thenReturn(lessonMarkEntity);

        final MarkAssignmentResponseDto responseDto = markService.assignMarkToStudentLesson(requestDto);

        assertEquals(instructorId, responseDto.instructor().id());
        assertEquals(studentId, responseDto.student().id());
        assertEquals(lessonId, responseDto.lesson().id());
        assertEquals(requestDto.mark(), responseDto.mark());
    }

    void testGetAverageLessonMarks(final Long studentId,
                                   final Long courseCode,
                                   final Set<LessonMarkEntity> lessonMarkEntities,
                                   final Map<Long, BigDecimal> expectedMarks) {
        final Long nonExistingCourseCode = courseCode + 1;

        when(lessonMarkRepository.findAllByStudentIdAndLessonCourseCode(studentId, courseCode)).thenReturn(lessonMarkEntities);

        final Map<Long, BigDecimal> actualMarks = markService.getAverageLessonMarksForStudentPerCourse(studentId, courseCode);

        expectedMarks.forEach((key, value) -> assertEquals(value, actualMarks.get(key)));
        assertEquals(expectedMarks, actualMarks);
        assertTrue(markService.getAverageLessonMarksForStudentPerCourse(studentId, nonExistingCourseCode).isEmpty());

    }

    void testGetStudentCourseMark(final Long studentId, final Long courseCode, final Map<Long, BigDecimal> lessonMarks) {
        final BigDecimal expectedMarkValue = lessonMarks.isEmpty() ? null : BigDecimal.valueOf(3.75);
        final Mark expectedMark = Mark.of(expectedMarkValue);

        when(markService.getAverageLessonMarksForStudentPerCourse(studentId, courseCode)).thenReturn(lessonMarks);

        final CourseMark studentCourseMark = markService.getStudentCourseMark(studentId, courseCode);

        assertEquals(studentId, studentCourseMark.getStudentId());
        assertEquals(courseCode, studentCourseMark.getCourseCode());
        assertEquals(lessonMarks, studentCourseMark.getLessonMarks());
        assertEquals(expectedMarkValue, studentCourseMark.getMarkValue());
        assertEquals(expectedMark, studentCourseMark.getMark());
    }

    private static Set<LessonMarkEntity> getLessonMarkEntities() {
        final LessonMarkEntity firstLessonFirstMark = Instancio.of(LessonMarkEntity.class)
                .set(field(LessonEntity::getId), 1L)
                .set(field(LessonMarkEntity::getMark), Mark.EXCELLENT)
                .create();
        final LessonMarkEntity firstLessonSecondMark = Instancio.of(LessonMarkEntity.class)
                .set(field(LessonEntity::getId), 1L)
                .set(field(LessonMarkEntity::getMark), Mark.AVERAGE)
                .create();
        final LessonMarkEntity firstLessonThirdMark = Instancio.of(LessonMarkEntity.class)
                .set(field(LessonEntity::getId), 1L)
                .set(field(LessonMarkEntity::getMark), Mark.POOR)
                .create();
        final LessonMarkEntity secondLessonFirstMark = Instancio.of(LessonMarkEntity.class)
                .set(field(LessonEntity::getId), 2L)
                .set(field(LessonMarkEntity::getMark), Mark.EXCELLENT)
                .create();
        final LessonMarkEntity secondLessonSecondMark = Instancio.of(LessonMarkEntity.class)
                .set(field(LessonEntity::getId), 2L)
                .set(field(LessonMarkEntity::getMark), Mark.ABOVE_AVERAGE)
                .create();
        return Set.of(firstLessonFirstMark, firstLessonSecondMark, firstLessonThirdMark, secondLessonFirstMark, secondLessonSecondMark);
    }
}