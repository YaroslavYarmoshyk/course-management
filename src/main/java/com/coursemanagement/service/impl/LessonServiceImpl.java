package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.UserLesson;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.UserLessonRepository;
import com.coursemanagement.repository.entity.LessonEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.repository.entity.UserLessonEntity;
import com.coursemanagement.rest.dto.UserLessonMarkRequestDto;
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
    private final UserLessonRepository userLessonRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Override
    public UserLesson createUserLesson(final Long userId, final Long lessonId) {
        final UserEntity studentEntity = mapper.map(userService.getById(userId), UserEntity.class);
        final LessonEntity lessonEntity = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new SystemException("Lesson with id: " + lessonId + " not found", SystemErrorCode.BAD_REQUEST));
        final UserLessonEntity userLessonEntity = new UserLessonEntity(studentEntity, lessonEntity);
        lessonEntity.getUserLessons().add(userLessonEntity);
        lessonRepository.save(lessonEntity);
        return getUserLesson(userId, lessonId);
    }

    @Override
    public UserLesson getUserLesson(final Long userId, final Long lessonId) {
        return userLessonRepository.findUserLessonEntityByStudentEntityIdAndLessonEntityId(userId, lessonId)
                .map(userLessonEntity -> mapper.map(userLessonEntity, UserLesson.class))
                .orElseThrow(() -> new SystemException(
                        "Cannot find lesson with id: " + lessonId + " assigned to user: " + userId,
                        SystemErrorCode.BAD_REQUEST)
                );
    }

    @Override
    public UserLesson markLesson(final UserLessonMarkRequestDto userLessonMarkRequestDto) {
        final Long studentId = userLessonMarkRequestDto.studentId();
        final Long lessonId = userLessonMarkRequestDto.lessonId();
        final Mark mark = userLessonMarkRequestDto.mark();
        final UserLesson userLesson = createUserLesson(studentId, lessonId);
        userLesson.setMark(mark);
        userLesson.setInstructor(userService.resolveCurrentUser());
        userLesson.setMarkAppliedAt(LocalDateTime.now(DEFAULT_ZONE_ID));

        userLessonRepository.save(mapper.map(userLesson, UserLessonEntity.class));
        return getUserLesson(studentId, lessonId);
    }
}
