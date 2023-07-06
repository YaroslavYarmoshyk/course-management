package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.LessonMarkEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LessonMarkRepository extends JpaRepository<LessonMarkEntity, Long> {

    Set<LessonMarkEntity> findAllByStudentIdAndLessonCourseCode(final Long studentId, final Long courseCode);

    @EntityGraph(attributePaths = {"student", "lesson", "instructor"})
    LessonMarkEntity findLessonMarkById(final Long lessonMarkId);
}
