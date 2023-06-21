package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.UserCourseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourseEntity, Long> {

    @EntityGraph(attributePaths = {"user" , "course"})
    List<UserCourseEntity> findByUserId(final Long userId);
}
