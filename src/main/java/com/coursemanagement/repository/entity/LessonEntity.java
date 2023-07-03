package com.coursemanagement.repository.entity;

import jakarta.persistence.Column;
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

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "course")
@Entity
@Table(name = "lesson")
public class LessonEntity {
    @Id
    @GeneratedValue(generator = "lesson_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "lesson_id_seq",
            sequenceName = "lesson_id_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_code",
            referencedColumnName = "code"
    )
    private CourseEntity course;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final LessonEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
