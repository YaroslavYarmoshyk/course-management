package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.model.mapper.ConfirmationTokenMapper;
import com.coursemanagement.model.mapper.UserMapper;
import com.coursemanagement.repository.ConfirmationTokenRepository;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    @Value("${token.confirmation.expiration-time.email}")
    private Long emailTokenExpirationTime;
    @Value("${token.confirmation.expiration-time.reset-password}")
    private Long resetPasswordTokenExpirationTime;

    @Override
    @Transactional
    public ConfirmationToken createEmailConfirmationToken(final User user) {
        return saveConfirmationToken(user, TokenType.EMAIL_CONFIRMATION, emailTokenExpirationTime, ChronoUnit.HOURS);
    }

    @Override
    public ConfirmationToken createResetPasswordToken(final User user) {
        return saveConfirmationToken(user, TokenType.RESET_PASSWORD, resetPasswordTokenExpirationTime, ChronoUnit.MINUTES);
    }

    @Override
    public ConfirmationToken findByTokenAndType(final String token, final TokenType type) {
        final Optional<ConfirmationTokenEntity> tokenEntity = tokenRepository.findByTokenAndType(token, type);
        if (tokenEntity.isPresent()) {
            return confirmationTokenMapper.entityToModel(tokenEntity.get());
        }
        throw new SystemException("Cannot find token: " + token + " in the database ", SystemErrorCode.BAD_REQUEST);
    }

    @Override
    public ConfirmationToken confirmToken(final String token, final TokenType type) {
        final ConfirmationToken confirmationToken = findByTokenAndType(token, type);
        if (isTokenValid(confirmationToken)) {
            invalidateToken(confirmationToken);
            log.info("{} sent by user with id {} is confirmed", type, confirmationToken.userId());
            return confirmationToken;
        }
        throw new SystemException(type + " sent by user with id " + confirmationToken.userId() + " is invalid", SystemErrorCode.BAD_REQUEST);
    }

    private void invalidateToken(final ConfirmationToken token) {
        final ConfirmationTokenEntity tokenEntity = confirmationTokenMapper.modelToEntity(token);
        tokenEntity.setActivated(Boolean.TRUE);
        tokenRepository.save(tokenEntity);
    }

    private ConfirmationToken saveConfirmationToken(final User user,
                                                    final TokenType type,
                                                    final long expirationTime,
                                                    final ChronoUnit expirationTimeUnit) {
        invalidateOldTokens(user, type);
        final ConfirmationToken newToken = new ConfirmationToken(
                user.getId(),
                type,
                getEncryptedToken(user),
                LocalDateTime.now(DEFAULT_ZONE_ID).plus(expirationTime, expirationTimeUnit),
                false
        );
        final ConfirmationTokenEntity confirmationTokenEntity = confirmationTokenMapper.modelToEntity(newToken);
        final ConfirmationTokenEntity savedToken = tokenRepository.save(confirmationTokenEntity);
        return confirmationTokenMapper.entityToModel(savedToken);
    }

    private void invalidateOldTokens(final User user, final TokenType tokenType) {
        final UserEntity userEntity = userMapper.modelToEntity(user);
        var oldTokens = tokenRepository.findAllByUserEntityAndType(userEntity, tokenType);
        oldTokens.forEach(token -> token.setActivated(Boolean.TRUE));
        tokenRepository.saveAll(oldTokens);
    }

    private String getEncryptedToken(final User user) {
        final String plainToken = String.format("%s%s", user.getEmail(), LocalDateTime.now(DEFAULT_ZONE_ID));
        return encryptionService.encrypt(plainToken);
    }

    private static boolean isTokenValid(final ConfirmationToken token) {
        return token.expirationDate().isAfter(LocalDateTime.now(DEFAULT_ZONE_ID))
                && Objects.equals(token.activated(), Boolean.FALSE);
    }
}
