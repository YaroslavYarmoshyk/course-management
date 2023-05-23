package com.coursemanagement.repository;

import com.coursemanagement.enumeration.RoleName;
import com.coursemanagement.repository.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Set<RoleEntity> findAllByNameIn(final Collection<RoleName> roleNames);
}
