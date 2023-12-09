package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.CourseFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CourseFeedbackRepository extends JpaRepository<CourseFeedbackEntity, Long> {

    Set<CourseFeedbackEntity> findAllByStudentIdAndCourseCode(final Long studentId, final Long courseCode);
}
