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

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class MarkServiceImpl implements MarkService {
    private final LessonMarkRepository lessonMarkRepository;
    private final ModelMapper mapper;
    private static final double ZERO_MARK_VALUE = 0.0;

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
    public CourseMark getStudentCourseMark(final Long studentId, final Long courseCode) {
        final Map<Long, Double> averageMarkPerLesson = lessonMarkRepository.findAllByStudentIdAndLessonCourseCode(studentId, courseCode).stream()
                .map(lessonMarkEntity -> mapper.map(lessonMarkEntity, LessonMark.class))
                .filter(lessonMark -> lessonMark.getMark() != null)
                .collect(Collectors.groupingBy(
                        LessonMark::getLessonId,
                        Collectors.averagingDouble(lessonMark -> lessonMark.getMark().getValue().doubleValue())
                ));

        double average = averageMarkPerLesson.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(ZERO_MARK_VALUE);
        final BigDecimal markValue = BigDecimal.valueOf(average);

        return CourseMark.courseMark()
                .withCourseCode(courseCode)
                .withStudentId(studentId)
                .withMarkValue(markValue)
                .withMark(Mark.of(markValue))
                .build();
    }
}
