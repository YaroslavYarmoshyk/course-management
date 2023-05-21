package com.coursemanagement.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "lesson")
public class LessonEntity {
    @Id
    @GeneratedValue(generator = "lesson_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "lesson_id_seq",
            sequenceName = "lesson_id_seq",
            allocationSize = 1
    )
    private Long id;
    @ManyToOne
    @JoinColumn(
            name = "course_code",
            referencedColumnName = "code"
    )
    private CourseEntity course;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "credits")
    private BigDecimal credits;
    @Column(name = "homework")
    private byte[] homework;
}
