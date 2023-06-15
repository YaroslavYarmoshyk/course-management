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
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString(exclude = "userLessonEntity")
@Entity
@Table(name = "homework_submission")
public class HomeworkSubmissionEntity {
    @Id
    @GeneratedValue(generator = "homework_submission_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "homework_submission_id_seq",
            sequenceName = "homework_submission_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "uploaded_date")
    private LocalDateTime uploadedDate;

    @Column(name = "homework")
    private byte[] homework;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_lesson_id")
    private UserLessonEntity userLessonEntity;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final HomeworkSubmissionEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
