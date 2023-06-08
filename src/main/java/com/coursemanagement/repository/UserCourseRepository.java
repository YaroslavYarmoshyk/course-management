package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.UserCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourseEntity, Long> {

    Optional<UserCourseEntity> findByUserEntityIdAndCourseEntityCode(final Long userId, final Long courseCode);
}
