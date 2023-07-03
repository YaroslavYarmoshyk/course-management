package com.coursemanagement.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "course_feedback")
public class CourseFeedbackEntity {
    @Id
    @GeneratedValue(generator = "course_feedback_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "course_feedback_id_seq",
            sequenceName = "course_feedback_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "course_code")
    private Long courseCode;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "instructor_id")
    private Long instructorId;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "feedback_submission_date")
    private LocalDateTime feedbackSubmissionDate;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final CourseFeedbackEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
