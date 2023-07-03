package com.coursemanagement.repository;

import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.entity.LessonContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface LessonContentRepository extends JpaRepository<LessonContentEntity, Long> {

    LessonContent findByFileId(final Long fileId);

    Set<LessonContent> findAllByLessonIdIn(final Collection<Long> lessonIds);
}
