package com.coursemanagement.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@ToString(exclude = "userCourseEntity")
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_course_id")
    private UserCourseEntity userCourseEntity;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "instructorId")
    private Long instructorId;

    @Column(name = "date")
    private LocalDateTime date;

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
