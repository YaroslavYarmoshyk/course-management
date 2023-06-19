package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserLesson;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.UserLessonRepository;
import com.coursemanagement.repository.entity.LessonEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.repository.entity.UserLessonEntity;
import com.coursemanagement.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final UserLessonRepository userLessonRepository;
    private final ModelMapper mapper;

    @Override
    public Set<Lesson> getAllByCourseCodes(final Collection<Long> courseCodes) {
        return lessonRepository.findAllByCourseCodeIn(courseCodes).stream()
                .map(lessonEntity -> mapper.map(lessonEntity, Lesson.class))
                .collect(Collectors.toSet());
    }

    @Override
    public UserLesson getUserLesson(final Long userId, final Long lessonId) {
        return userLessonRepository.findUserLessonEntityByUserEntityIdAndLessonEntityId(userId, lessonId)
                .map(userLessonEntity -> mapper.map(userLessonEntity, UserLesson.class))
                .orElseThrow(() -> new SystemException(
                        "Cannot find lesson with id: " + lessonId + " assigned to user: " + userId,
                        SystemErrorCode.BAD_REQUEST)
                );
    }

    @Override
    public void addUserToLessons(final User user, final Collection<Lesson> lessons) {
        final UserEntity userEntity = mapper.map(user, UserEntity.class);
        final Set<Long> lessonIds = lessons.stream()
                .map(Lesson::getId)
                .collect(Collectors.toSet());
        final Set<LessonEntity> lessonEntities = lessonRepository.findAllByIdIn(lessonIds);
        for (final LessonEntity lessonEntity : lessonEntities) {
            final UserLessonEntity userLessonEntity = new UserLessonEntity(userEntity, lessonEntity);
            lessonEntity.getUserLessons().add(userLessonEntity);
        }
    }
}
