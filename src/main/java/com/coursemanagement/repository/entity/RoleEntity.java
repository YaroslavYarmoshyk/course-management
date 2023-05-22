package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.RoleName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "role")
@NoArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(generator = "role_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "role_id_seq",
            sequenceName = "role_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "name")
    private RoleName name;

    public RoleEntity(final RoleName name) {
        this.name = name;
    }
}
