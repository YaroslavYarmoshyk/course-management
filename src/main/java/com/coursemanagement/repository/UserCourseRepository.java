package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.UserCourseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourseEntity, Long> {

    @EntityGraph(attributePaths = {"userEntity" , "courseEntity"})
    List<UserCourseEntity> findByUserEntityId(final Long userId);
}
