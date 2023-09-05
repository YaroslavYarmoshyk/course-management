package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.UserCourseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourseEntity, Long> {

    @EntityGraph(attributePaths = {"course", "course.userCourses", "course.userCourses.roles"})
    List<UserCourseEntity> findByUserId(final Long userId);

    @Query(name = "findStudentsByCourseCode")
    List<UserCourseEntity> findStudentsByCourseCode(final Long courseCode);

    @EntityGraph(attributePaths = {"user", "course"})
    Optional<UserCourseEntity> findByUserIdAndCourseCode(final Long userId, final Long courseCode);
}
