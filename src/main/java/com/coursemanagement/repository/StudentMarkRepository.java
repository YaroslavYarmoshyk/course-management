package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.StudentMarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentMarkRepository extends JpaRepository<StudentMarkEntity, Long> {

    Optional<StudentMarkEntity> findByStudentIdAndLessonId(final Long studentId, final Long lessonId);
}
