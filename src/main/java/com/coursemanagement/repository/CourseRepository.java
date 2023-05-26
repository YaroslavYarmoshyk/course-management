package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.CourseEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @EntityGraph(attributePaths = {"users"})
    Optional<CourseEntity> findByCode(@NonNull final Long code);
}
