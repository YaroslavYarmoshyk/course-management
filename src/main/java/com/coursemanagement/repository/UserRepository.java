package com.coursemanagement.repository;

import com.coursemanagement.repository.entity.UserEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByEmail(final String email);

    @NonNull
    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findById(@NonNull final Long id);

    @NonNull
    @EntityGraph(attributePaths = "roles")
    List<UserEntity> findAll();
}
