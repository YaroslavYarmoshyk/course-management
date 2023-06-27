package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.CourseFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseFeedbackRepository extends JpaRepository<CourseFeedbackEntity, Long> {
}
