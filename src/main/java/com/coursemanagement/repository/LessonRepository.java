package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {

    Set<LessonEntity> findAllByCourseCodeIn(final Collection<Long> courseCodes);

    Set<LessonEntity> findAllByIdIn(final Collection<Long> lessonIds);
}
