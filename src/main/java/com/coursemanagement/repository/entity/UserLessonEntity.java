package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.converter.MarkEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@ToString(exclude = {"userEntity", "lessonEntity"})
@Entity
@Table(name = "user_lesson")
public class UserLessonEntity {
    @EmbeddedId
    private UserLessonId userLessonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userEntityId")
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lessonEntityId")
    @JoinColumn(name = "lesson_id")
    private LessonEntity lessonEntity;

    @Column(name = "mark")
    @Convert(converter = MarkEnumConverter.class)
    private Mark mark;

    public UserLessonEntity(final UserEntity userEntity, final LessonEntity lessonEntity) {
        this.userEntity = userEntity;
        this.lessonEntity = lessonEntity;
        this.userLessonId = new UserLessonId(userEntity.getId(), lessonEntity.getId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final UserLessonEntity other)) {
            return false;
        }
        return userLessonId != null && Objects.equals(userLessonId, other.userLessonId);
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
    public static class UserLessonId implements Serializable {
        private Long userEntityId;
        private Long lessonEntityId;
    }
}
