package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.LessonMark;
import com.coursemanagement.repository.LessonMarkRepository;
import com.coursemanagement.repository.entity.LessonMarkEntity;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserInfoDto;
import com.coursemanagement.service.MarkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
                new UserInfoDto(lessonMarkEntity.getStudent()),
                new LessonDto(lessonMarkEntity.getLesson()),
                new UserInfoDto(lessonMarkEntity.getInstructor()),
                lessonMarkEntity.getMark(),
                lessonMarkEntity.getMarkSubmissionDate()
        );
    }

    //    TODO: Provide entire implementation of this method
    @Override
    public CourseMark getStudentCourseMark(final Long studentId, final Long courseCode) {
        final List<LessonMarkEntity> lessonMarks = lessonMarkRepository.findAllByStudentIdAndLessonCourseCode(studentId, courseCode);
        final double courseMark = lessonMarks.stream()
                .map(LessonMarkEntity::getMark)
                .map(Mark::getValue)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(Double.NaN);
        return new CourseMark().setCourseCode(courseCode)
                .setStudentId(studentId)
                .setMarkValue(BigDecimal.valueOf(courseMark))
                .setMark(null);
    }
}
