package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {

    Set<LessonEntity> findAllByCourseCode(final Long courseCode);

    boolean existsByCourseUserCoursesUserIdAndId(final Long userId, final Long lessonId);
}
