package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.LessonContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonContentRepository extends JpaRepository<LessonContentEntity, Long> {

}
