package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.converter.MarkEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "student_mark")
public class StudentMarkEntity {
    @Id
    @GeneratedValue(generator = "student_mark_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "student_mark_id_seq",
            sequenceName = "student_mark_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "mark")
    @Convert(converter = MarkEnumConverter.class)
    private Mark mark;

    @Column(name = "mark_submission_date")
    private LocalDateTime markSubmissionDate;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "instructor_id")
    private Long instructorId;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final StudentMarkEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
