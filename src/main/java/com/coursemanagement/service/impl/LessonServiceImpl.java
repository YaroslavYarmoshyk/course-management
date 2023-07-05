package com.coursemanagement.service.impl;

import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.entity.LessonEntity;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final LessonContentRepository lessonContentRepository;

    @Override
    public boolean isUserAssociatedWithLesson(final Long userId, final Long lessonId) {
        return lessonRepository.existsByCourseUserCoursesUserIdAndId(userId, lessonId);
    }

    @Override
    public boolean isUserAssociatedWithLessonFile(final Long userId, final Long fileId) {
        final LessonContent lessonContent = lessonContentRepository.findByFileId(fileId);
        final Long lessonId = lessonContent.getLessonId();
        return isUserAssociatedWithLesson(userId, lessonId);
    }

    @Override
    public Set<LessonDto> getLessonsWithContentPerCourse(final Long userId, final Long courseCode) {
        final Set<LessonEntity> lessonEntities = lessonRepository.findAllByCourseCode(courseCode);
        final Set<Long> lessonIds = lessonEntities.stream()
                .map(LessonEntity::getId)
                .collect(Collectors.toSet());

        final Map<Long, Set<LessonContent>> lessonContents = lessonContentRepository.findAllByLessonIdIn(lessonIds).stream()
                .collect(Collectors.groupingBy(LessonContent::getLessonId, Collectors.toSet()));

        return lessonEntities.stream()
                .map(lessonEntity -> new LessonDto(
                        lessonEntity.getId(),
                        lessonEntity.getTitle(),
                        lessonEntity.getDescription(),
                        lessonContents.get(lessonEntity.getId())))
                .collect(Collectors.toSet());

    }
}
