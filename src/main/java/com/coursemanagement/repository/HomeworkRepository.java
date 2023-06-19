package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.HomeworkSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkRepository extends JpaRepository<HomeworkSubmissionEntity, Long> {
}
