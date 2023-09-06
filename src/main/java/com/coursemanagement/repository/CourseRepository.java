package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.CourseEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @EntityGraph(attributePaths = {"userCourses", "userCourses.user", "userCourses.user.roles"})
    Optional<CourseEntity> findByCode(@NonNull final Long code);

    @EntityGraph(attributePaths = "userCourses")
    Set<CourseEntity> findAllByCodeIn(final Collection<Long> codes);

    boolean existsByUserCoursesUserIdAndCode(final Long userId, final Long courseCode);
}
