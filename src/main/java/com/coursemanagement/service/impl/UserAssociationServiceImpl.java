package com.coursemanagement.service.impl;

import com.coursemanagement.model.LessonContent;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.service.UserAssociationService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.coursemanagement.util.AuthorizationUtils.isCurrentUserAdmin;

@Service
@RequiredArgsConstructor
public class UserAssociationServiceImpl implements UserAssociationService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonContentRepository lessonContentRepository;
    private final UserService userService;

    @Override
    public boolean currentUserHasAccessTo(final Long userId) {
        final User currentUser = userService.resolveCurrentUser();
        return isCurrentUserAdmin() || Objects.equals(currentUser.getId(), userId);
    }

    @Override
    public boolean isUserAssociatedWithCourse(final Long userId, final Long courseCode) {
        return isCurrentUserAdmin() || courseRepository.existsByUserCoursesUserIdAndCode(userId, courseCode);
    }

    @Override
    public boolean isUserAssociatedWithLesson(final Long userId, final Long lessonId) {
        return isCurrentUserAdmin() || lessonRepository.existsByCourseUserCoursesUserIdAndId(userId, lessonId);
    }

    @Override
    public boolean isUserAssociatedWithLessonFile(final Long userId, final Long fileId) {
        final LessonContent lessonContent = lessonContentRepository.findByFileId(fileId);
        final Long lessonId = lessonContent.getLessonId();
        return isUserAssociatedWithLesson(userId, lessonId);
    }
}
