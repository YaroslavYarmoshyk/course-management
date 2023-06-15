package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.enumeration.converter.MarkEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@ToString(exclude = {"userEntity", "lessonEntity"})
@Entity
@Table(name = "user_lesson")
public class UserLessonEntity {
    @Id
    @GeneratedValue(generator = "user_lesson_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "user_lesson_id_seq",
            sequenceName = "user_lesson_id_seq",
            allocationSize = 1
    )
    private Long id;

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
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final UserLessonEntity other)) {
            return false;
        }
        return Objects.equals(userEntity.getId(), other.getUserEntity().getId())
                && Objects.equals(lessonEntity.getId(), other.lessonEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
