package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.HomeworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkRepository extends JpaRepository<HomeworkEntity, Long> {

    HomeworkEntity findByStudentIdAndLessonId(final Long studentId, final Long lessonId);
}
