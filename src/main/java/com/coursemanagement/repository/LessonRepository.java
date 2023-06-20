package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.LessonEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {

    @NonNull
    @EntityGraph(attributePaths = "userLessons")
    Optional<LessonEntity> findById(@NonNull final Long lessonId);
}
