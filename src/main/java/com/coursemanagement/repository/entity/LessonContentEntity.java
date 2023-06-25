package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.LessonPart;
import com.coursemanagement.enumeration.converter.LessonPartEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "lesson_content")
public class LessonContentEntity {
    @Id
    @GeneratedValue(generator = "lesson_content_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "lesson_content_id_seq",
            sequenceName = "lesson_content_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "lesson_part")
    @Convert(converter = LessonPartEnumConverter.class)
    private LessonPart lessonPart;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final LessonContentEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
