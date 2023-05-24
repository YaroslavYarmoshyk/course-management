package com.coursemanagement.service.impl;

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
import org.springframework.http.HttpStatus;
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
                LocalDateTime.now(DEFAULT_ZONE_ID).plus(emailTokenExpirationTime, ChronoUnit.HOURS),
                false
        );
        final ConfirmationTokenEntity confirmationTokenEntity = confirmationTokenMapper.modelToEntity(newToken);
        final ConfirmationTokenEntity savedToken = tokenRepository.save(confirmationTokenEntity);
        return confirmationTokenMapper.entityToModel(savedToken);
    }

    @Override
    public ConfirmationToken findByTokenAndType(final String token, final TokenType type) {
        final Optional<ConfirmationTokenEntity> tokenEntity = tokenRepository.findByTokenAndType(token, type);
        if (tokenEntity.isPresent()) {
            return confirmationTokenMapper.entityToModel(tokenEntity.get());
        }
        throw new SystemException("Cannot find token: " + token + " in the database ", HttpStatus.BAD_REQUEST);
    }

    private String getEncryptedEmailConfirmationToken(final User user) {
        final String plainToken = String.format("%s%s", user.getEmail(), LocalDateTime.now(DEFAULT_ZONE_ID));
        return encryptionService.encrypt(plainToken);
    }

    @Override
    public void invalidateToken(final ConfirmationToken token) {
        final Optional<ConfirmationTokenEntity> tokenEntity = tokenRepository.findById(token.id());
        tokenEntity.ifPresent(tokenFromDb -> {
            tokenFromDb.setActivated(Boolean.TRUE);
            tokenRepository.save(tokenFromDb);
        });
    }

    @Override
    public boolean isTokenValid(final ConfirmationToken token) {
        return token.expirationDate().isAfter(LocalDateTime.now(DEFAULT_ZONE_ID))
                && Objects.equals(token.activated(), Boolean.FALSE);
    }

    private void invalidateOldTokens(final User user, final TokenType tokenType) {
        final UserEntity userEntity = userMapper.modelToEntity(user);
        var oldTokens = tokenRepository.findAllByUserEntityAndType(userEntity, tokenType);
        oldTokens.forEach(token -> token.setActivated(Boolean.FALSE));
        tokenRepository.saveAll(oldTokens);
    }
}
