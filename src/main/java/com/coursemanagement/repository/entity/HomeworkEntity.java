package com.coursemanagement.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "homework")
public class HomeworkEntity {
    @Id
    @GeneratedValue(generator = "homework_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "homework_id_seq",
            sequenceName = "homework_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "uploaded_date")
    private LocalDateTime uploadedDate;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "student_id")
    private Long studentId;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final HomeworkEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
