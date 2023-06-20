package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.converter.MarkEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"studentEntity", "lessonEntity", "instructorEntity"})
@Entity
@Table(name = "user_lesson")
public class UserLessonEntity {
    @Id
    @GeneratedValue(generator = "user_lesson_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "user_lesson_id_seq",
            sequenceName = "user_lesson_id_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private UserEntity studentEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private LessonEntity lessonEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private UserEntity instructorEntity;

    @Column(name = "mark")
    @Convert(converter = MarkEnumConverter.class)
    private Mark mark;

    @Column(name = "mark_applied_at")
    private LocalDateTime markAppliedAt;

    public UserLessonEntity(final UserEntity studentEntity, final LessonEntity lessonEntity) {
        this.studentEntity = studentEntity;
        this.lessonEntity = lessonEntity;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final UserLessonEntity other)) {
            return false;
        }
        return Objects.equals(studentEntity.getId(), other.getStudentEntity().getId())
                && Objects.equals(lessonEntity.getId(), other.getLessonEntity().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentEntity.getId(), lessonEntity.getId());
    }
}
