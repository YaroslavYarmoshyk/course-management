package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.LessonContent;
import com.coursemanagement.model.StudentMark;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.StudentMarkRepository;
import com.coursemanagement.repository.entity.StudentMarkEntity;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserInfoDto;
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
    private final LessonContentRepository lessonContentRepository;
    private final StudentMarkRepository studentMarkRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Override
    public Lesson getLessonById(final Long lessonId) {
        return lessonRepository.findById(lessonId)
                .map(lessonEntity -> mapper.map(lessonEntity, Lesson.class))
                .orElseThrow(() -> new SystemException("Cannot find lesson with id: " + lessonId, SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public MarkAssignmentResponseDto assignMarkToUserLesson(final MarkAssigmentRequestDto markAssigmentRequestDto) {
        final Long studentId = markAssigmentRequestDto.studentId();
        final Long lessonId = markAssigmentRequestDto.lessonId();
        final StudentMark studentMark = studentMarkRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .map(studentMarkEntity -> mapper.map(studentMarkEntity, StudentMark.class))
                .orElse(new StudentMark(studentId, lessonId));
        final Mark mark = markAssigmentRequestDto.mark();
        final Long instructorId = userService.resolveCurrentUser().getId();
        studentMark.setMark(mark);
        studentMark.setMarkSubmissionDate(LocalDateTime.now(DEFAULT_ZONE_ID));
        studentMark.setInstructorId(instructorId);
        final StudentMarkEntity savedStudentMark = studentMarkRepository.save(mapper.map(studentMark, StudentMarkEntity.class));
        return getMarkAssignmentResponseDto(mapper.map(savedStudentMark, StudentMark.class));
    }

    @Override
    public boolean isUserAssociatedWithLesson(final Long userId, final Long lessonId) {
        return lessonRepository.isUserAssociatedWithLesson(userId, lessonId);
    }

    @Override
    public boolean isUserAssociatedWithLessonFile(final Long userId, final Long fileId) {
        final LessonContent lessonContent = lessonContentRepository.findByFileId(fileId);
        final Long lessonId = lessonContent.getLessonId();
        return isUserAssociatedWithLesson(userId, lessonId);
    }

    private MarkAssignmentResponseDto getMarkAssignmentResponseDto(final StudentMark studentMark) {
        final UserInfoDto student = new UserInfoDto(userService.getUserById(studentMark.getStudentId()));
        final UserInfoDto instructor = new UserInfoDto(userService.getUserById(studentMark.getInstructorId()));
        final LessonDto lesson = new LessonDto(getLessonById(studentMark.getLessonId()));
        return new MarkAssignmentResponseDto(
                student,
                lesson,
                instructor,
                studentMark.getMark(),
                studentMark.getMarkSubmissionDate()
        );
    }
}
