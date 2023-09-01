package com.coursemanagement.service.impl;

import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.service.UserAssociationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAssociationServiceImpl implements UserAssociationService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonContentRepository lessonContentRepository;

    @Override
    public boolean isUserAssociatedWithCourse(final Long userId, final Long courseCode) {
        return courseRepository.existsByUserCoursesUserIdAndCode(userId, courseCode);
    }

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
}
