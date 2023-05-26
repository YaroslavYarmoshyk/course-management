package com.coursemanagement.repository;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.repository.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Set<RoleEntity> findAllByRoleIn(final Collection<Role> roles);
}
