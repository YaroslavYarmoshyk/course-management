package com.coursemanagement.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"userEntity", "courseEntity"})
@Entity
@Table(name = "user_course")
public class UserCourseEntity {
    @EmbeddedId
    private UserCourseId userCourseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userEntityId")
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseEntityCode")
    @JoinColumn(name = "course_code")
    private CourseEntity courseEntity;

    @Column(name = "finished")
    private boolean finished;

    public UserCourseEntity(final UserEntity userEntity, final CourseEntity courseEntity) {
        this.userEntity = userEntity;
        this.courseEntity = courseEntity;
        this.userCourseId = new UserCourseId(userEntity.getId(), courseEntity.getCode());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final UserCourseEntity other)) {
            return false;
        }
        return userCourseId != null && Objects.equals(userCourseId, other.userCourseId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCourseId implements Serializable {
        private Long userEntityId;
        private Long courseEntityCode;
    }
}
