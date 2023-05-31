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
@ToString(exclude = {"userEntity", "courseEntity"})
@Entity
@Table(name = "user_course")
public class UserCourseEntity {
    @Id
    @GeneratedValue(generator = "user_course_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "user_course_id_seq",
            sequenceName = "user_course_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code")
    private CourseEntity courseEntity;

    @Column(name = "finished")
    private boolean finished;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final UserCourseEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
