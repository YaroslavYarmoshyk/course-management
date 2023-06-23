package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {

    @Query(value = """
            SELECT CASE WHEN EXISTS (
                SELECT 1 FROM LessonEntity l
                JOIN l.course c
                JOIN c.userCourses us
                WHERE us.user.id = :userId
                AND l.id = :lessonId
            ) THEN true ELSE false END
            """)
    boolean isUserAssociatedWithLesson(@Param("userId") Long userId, @Param("lessonId") Long lessonId);

}
