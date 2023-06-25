package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.LessonMarkEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonMarkRepository extends JpaRepository<LessonMarkEntity, Long> {

    @EntityGraph(attributePaths = {"student", "lesson", "lesson.course"})
    List<LessonMarkEntity> findAllByStudentIdAndLessonCourseCode(final Long userId, final Long courseCode);

    @EntityGraph(attributePaths = {"student", "lesson", "instructor"})
    LessonMarkEntity findLessonMarkById(final Long lessonMarkId);
}
