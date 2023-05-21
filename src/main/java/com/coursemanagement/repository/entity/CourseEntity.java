package com.coursemanagement.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "course")
public class CourseEntity {
    @Id
    @Column(name = "code")
    private Long code;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
}
