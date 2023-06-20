package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.UserLessonEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLessonRepository extends JpaRepository<UserLessonEntity, Long> {

    @EntityGraph(attributePaths = {"studentEntity", "lessonEntity", "instructorEntity"})
    Optional<UserLessonEntity> findUserLessonEntityByStudentEntityIdAndLessonEntityId(final Long studentId, final Long lessonId);
}
