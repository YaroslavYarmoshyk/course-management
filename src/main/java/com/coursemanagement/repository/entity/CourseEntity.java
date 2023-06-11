package com.coursemanagement.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NamedEntityGraph(
        name = "course-userCourse-user-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "userCourses", subgraph = "user-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "user-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("userEntity")
                        }
                )
        }
)
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "userCourses")
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

    @OneToMany(
            mappedBy = "courseEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UserCourseEntity> userCourses = new HashSet<>();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final CourseEntity other)) {
            return false;
        }
        return code != null && Objects.equals(code, other.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
