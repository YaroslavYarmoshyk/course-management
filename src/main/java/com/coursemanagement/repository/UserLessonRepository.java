package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.UserLessonEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLessonRepository extends JpaRepository<UserLessonEntity, Long> {

    @EntityGraph(attributePaths = {"userEntity", "lessonEntity"})
    Optional<UserLessonEntity> findUserLessonEntityByUserEntityIdAndLessonEntityId(final Long userId, final Long lessonId);
}
