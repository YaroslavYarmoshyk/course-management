package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.LessonMark;
import com.coursemanagement.repository.LessonMarkRepository;
import com.coursemanagement.repository.entity.LessonMarkEntity;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.MarkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static com.coursemanagement.util.Constants.*;
import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class MarkServiceImpl implements MarkService {
    private final LessonMarkRepository lessonMarkRepository;
    private final ModelMapper mapper;

    @Override
    public MarkAssignmentResponseDto assignMarkToUserLesson(final Long instructorId,
                                                            final MarkAssigmentRequestDto markAssigmentRequestDto) {
        final Long studentId = markAssigmentRequestDto.studentId();
        final Long lessonId = markAssigmentRequestDto.lessonId();

        final Mark mark = markAssigmentRequestDto.mark();
        final LessonMark lessonMark = new LessonMark().setStudentId(studentId)
                .setLessonId(lessonId)
                .setMark(mark)
                .setMarkSubmissionDate(LocalDateTime.now(DEFAULT_ZONE_ID))
                .setInstructorId(instructorId);
        final LessonMarkEntity savedLessonMarkEntity = lessonMarkRepository.save(mapper.map(lessonMark, LessonMarkEntity.class));
        final LessonMarkEntity lessonMarkEntity = lessonMarkRepository.findLessonMarkById(savedLessonMarkEntity.getId());
        return getMarkAssignmentResponseDto(lessonMarkEntity);
    }

    private static MarkAssignmentResponseDto getMarkAssignmentResponseDto(final LessonMarkEntity lessonMarkEntity) {
        return new MarkAssignmentResponseDto(
                new UserDto(lessonMarkEntity.getStudent()),
                new LessonDto(lessonMarkEntity.getLesson()),
                new UserDto(lessonMarkEntity.getInstructor()),
                lessonMarkEntity.getMark(),
                lessonMarkEntity.getMarkSubmissionDate()
        );
    }

    @Override
    public Map<Long, BigDecimal> getAverageLessonMarksForStudentPerCourse(final Long studentId, final Long courseCode) {
        return lessonMarkRepository.findAllByStudentIdAndLessonCourseCode(studentId, courseCode).stream()
                .map(lessonMarkEntity -> mapper.map(lessonMarkEntity, LessonMark.class))
                .collect(Collectors.groupingBy(
                        LessonMark::getLessonId,
                        Collectors.collectingAndThen(
                                Collectors.averagingDouble(mark -> mark.getMark().getValue().doubleValue()),
                                doubleValue -> BigDecimal.valueOf(doubleValue).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE)
                        )
                ));
    }

    @Override
    public CourseMark getStudentCourseMark(final Long studentId, final Long courseCode) {
        final Map<Long, BigDecimal> averageLessonMarks = getAverageLessonMarksForStudentPerCourse(studentId, courseCode);
        final BigDecimal courseMarkValue = getCourseMarkValue(averageLessonMarks);
        final Mark courseMark = Mark.of(courseMarkValue);

        return CourseMark.courseMark()
                .withCourseCode(courseCode)
                .withStudentId(studentId)
                .withLessonMarks(averageLessonMarks)
                .withMarkValue(courseMarkValue)
                .withMark(courseMark)
                .build();
    }

    private static BigDecimal getCourseMarkValue(final Map<Long, BigDecimal> averageLessonMarks) {
        if (averageLessonMarks.isEmpty()) {
            return null;
        }
        final double finalMarkValue = averageLessonMarks.values().stream()
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(ZERO_MARK_VALUE);
        return BigDecimal.valueOf(finalMarkValue).setScale(MARK_ROUNDING_SCALE, MARK_ROUNDING_MODE);
    }
}
