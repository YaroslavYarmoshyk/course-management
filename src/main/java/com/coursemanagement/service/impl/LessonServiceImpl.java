package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Grade;
import com.coursemanagement.repository.GradeRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.entity.GradeEntity;
import com.coursemanagement.rest.dto.GradeAssigmentRequestDto;
import com.coursemanagement.rest.dto.GradeAssignmentResponseDto;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final GradeRepository gradeRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Override
    public LessonDto getById(final Long lessonId) {
        return lessonRepository.findById(lessonId)
                .map(LessonDto::new)
                .orElseThrow(() -> new SystemException("Cannot find lesson with id: " + lessonId, SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public GradeAssignmentResponseDto assignGradeToUserLesson(final GradeAssigmentRequestDto gradeAssigmentRequestDto) {
        final Long studentId = gradeAssigmentRequestDto.studentId();
        final Long lessonId = gradeAssigmentRequestDto.lessonId();
        final Grade grade = gradeRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .map(gradeEntity -> mapper.map(gradeEntity, Grade.class))
                .orElse(new Grade(studentId, lessonId));
        final Mark mark = gradeAssigmentRequestDto.mark();
        final Long instructorId = userService.resolveCurrentUser().getId();
        grade.setMark(mark);
        grade.setMarkSubmissionDate(LocalDateTime.now(DEFAULT_ZONE_ID));
        grade.setInstructorId(instructorId);
        final GradeEntity savedGrade = gradeRepository.save(mapper.map(grade, GradeEntity.class));
        return gerGradeAssignmentResponseDto(mapper.map(savedGrade, Grade.class));
    }

    private GradeAssignmentResponseDto gerGradeAssignmentResponseDto(final Grade grade) {
        final UserDto student = new UserDto(userService.getById(grade.getStudentId()));
        final UserDto instructor = new UserDto(userService.getById(grade.getInstructorId()));
        final LessonDto lesson = getById(grade.getLessonId());
        return new GradeAssignmentResponseDto(
                student,
                lesson,
                instructor,
                grade.getMark(),
                grade.getMarkSubmissionDate()
        );
    }
}
