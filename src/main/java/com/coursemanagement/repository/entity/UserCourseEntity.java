package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.enumeration.converter.UserCourseStatusEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"user", "course"})
@Entity
@Table(name = "user_course")
@NamedQuery(
        name = "findStudentsByCourseCode",
        query = "SELECT uc FROM UserCourseEntity uc JOIN FETCH uc.user u JOIN FETCH u.roles ur JOIN FETCH uc.course c WHERE c.code =:courseCode AND ur.role = 'STUDENT'"
)
public class UserCourseEntity {
    @EmbeddedId
    private UserCourseEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "courseCode")
    private CourseEntity course;

    @Convert(converter = UserCourseStatusEnumConverter.class)
    @Column(name = "status")
    private UserCourseStatus status = UserCourseStatus.STARTED;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate = LocalDateTime.now(DEFAULT_ZONE_ID);

    @Column(name = "accomplishment_date")
    private LocalDateTime accomplishmentDate;


    public UserCourseEntity(final UserEntity user, final CourseEntity course) {
        this.user = user;
        this.course = course;
        this.id = new UserCourseEntityId(user.getId(), course.getCode());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final UserCourseEntity other)) {
            return false;
        }
        return Objects.equals(user.getId(), other.getUser().getId())
                && Objects.equals(course.getCode(), other.getCourse().getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, course);
    }

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class UserCourseEntityId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "course_code")
        private Long courseCode;
    }
}
