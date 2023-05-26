package com.coursemanagement.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Set;

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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_course",
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            joinColumns = @JoinColumn(name = "course_code", referencedColumnName = "code")
    )
    private Set<UserEntity> users;
}
