package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.entity.LessonEntity;
import com.coursemanagement.rest.dto.LessonDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.util.AuthorizationUtil;
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
    private final CourseService courseService;

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
    public Set<LessonDto> getLessonsPerCourse(final Long courseCode, final Long userId) {
        checkUserAccessToCourse(userId, courseCode);
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

    private void checkUserAccessToCourse(Long userId, Long courseCode) {
        if (AuthorizationUtil.isAdminOrInstructor()) {
            return;
        }

        if (!courseService.isUserAssociatedWithCourse(userId, courseCode)) {
            throw new SystemException("Access to the lesson is limited to associated students only", SystemErrorCode.FORBIDDEN);
        }
    }
}
