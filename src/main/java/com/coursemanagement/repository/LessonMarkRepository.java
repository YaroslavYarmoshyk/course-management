package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.LessonMarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonMarkRepository extends JpaRepository<LessonMarkEntity, Long> {
}
