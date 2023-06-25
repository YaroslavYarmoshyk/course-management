package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.LessonContent;
import com.coursemanagement.model.LessonMark;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserInfoDto;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.MarkService;
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
    private final UserService userService;
    private final MarkService markService;
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
        final Mark mark = markAssigmentRequestDto.mark();
        final Long instructorId = userService.resolveCurrentUser().getId();
        final LessonMark lessonMark = new LessonMark().setStudentId(studentId)
                .setLessonId(lessonId)
                .setMark(mark)
                .setMarkSubmissionDate(LocalDateTime.now(DEFAULT_ZONE_ID))
                .setInstructorId(instructorId);
        final LessonMark savedLessonMark = markService.save(lessonMark);
        return getMarkAssignmentResponseDto(savedLessonMark);
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

    private MarkAssignmentResponseDto getMarkAssignmentResponseDto(final LessonMark lessonMark) {
        final UserInfoDto student = new UserInfoDto(userService.getUserById(lessonMark.getStudentId()));
        final UserInfoDto instructor = new UserInfoDto(userService.getUserById(lessonMark.getInstructorId()));
        final LessonDto lesson = new LessonDto(getLessonById(lessonMark.getLessonId()));
        return new MarkAssignmentResponseDto(
                student,
                lesson,
                instructor,
                lessonMark.getMark(),
                lessonMark.getMarkSubmissionDate()
        );
    }
}
