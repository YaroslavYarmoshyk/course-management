package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.model.mapper.ConfirmationTokenMapper;
import com.coursemanagement.model.mapper.UserMapper;
import com.coursemanagement.repository.ConfirmationTokenRepository;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.EncryptionService;
import com.coursemanagement.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final EncryptionService encryptionService;
    private final ConfirmationTokenRepository tokenRepository;
    private final ConfirmationTokenMapper confirmationTokenMapper;
    private final UserMapper userMapper;
    @Value("${token.confirmation.email.expiration-time}")
    private Long emailTokenExpirationTime;

    @Override
    @Transactional
    public ConfirmationToken createEmailConfirmationToken(final User user) {
        invalidateOldTokens(user, TokenType.EMAIL_CONFIRMATION);
        final ConfirmationToken newToken = new ConfirmationToken(
                null,
                user.getId(),
                TokenType.EMAIL_CONFIRMATION,
                getEncryptedEmailConfirmationToken(user),
                LocalDateTime.now(DEFAULT_ZONE_ID).plusHours(emailTokenExpirationTime),
                true
        );
        final ConfirmationTokenEntity confirmationTokenEntity = confirmationTokenMapper.modelToEntity(newToken);
        tokenRepository.save(confirmationTokenEntity);
        return newToken;
    }

    private String getEncryptedEmailConfirmationToken(final User user) {
        final String plainToken = String.format("%s%s", user.getEmail(), LocalDateTime.now(DEFAULT_ZONE_ID));
        return encryptionService.encrypt(plainToken);
    }

    @Override
    public boolean isConfirmationTokenValid(final String token, final TokenType type) {
        try {
            final String encryptedToken = encryptionService.encrypt(token);
            final var tokenEntity = tokenRepository.findByTokenAndType(encryptedToken, type);
            if (tokenEntity.isEmpty()) {
                log.info("Cannot find token: {} in the database", encryptedToken);
                return false;
            }
            final ConfirmationToken tokenFromDb = confirmationTokenMapper.modelToModel(tokenEntity.get());
            return isValidByUserAndExpiration(tokenFromDb);
        } catch (Exception e) {
            log.warn("Cannot validate token: {}", token);
            return false;
        }
    }

    @Override
    public void invalidateToken(final ConfirmationToken token) {
        final Optional<ConfirmationTokenEntity> tokenEntity = tokenRepository.findById(token.id());
        tokenEntity.ifPresent(tokenFromDb -> {
            tokenFromDb.setActive(Boolean.FALSE);
            tokenRepository.save(tokenFromDb);
        });
    }

    private boolean isValidByUserAndExpiration(final ConfirmationToken token) {
        final User user = UserUtils.resolveCurrentUser();
        return token.expirationDate().isBefore(LocalDateTime.now(DEFAULT_ZONE_ID))
                && Objects.equals(token.userId(), user.getId());
    }

    private void invalidateOldTokens(final User user, final TokenType tokenType) {
        final UserEntity userEntity = userMapper.modelToEntity(user);
        var oldTokens = tokenRepository.findAllByUserEntityAndType(userEntity, tokenType);
        oldTokens.forEach(token -> token.setActive(Boolean.FALSE));
        tokenRepository.saveAll(oldTokens);
    }
}
