package com.coursemanagement.repository;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenEntity, Long> {

    Set<ConfirmationTokenEntity> findAllByUserIdAndType(final Long userId, final TokenType type);

    Optional<ConfirmationTokenEntity> findByTokenAndType(final String token, final TokenType type);
}
