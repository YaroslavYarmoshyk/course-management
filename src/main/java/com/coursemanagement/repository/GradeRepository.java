package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Long> {

    Optional<GradeEntity> findByStudentIdAndLessonId(final Long studentId, final Long lessonId);
}
