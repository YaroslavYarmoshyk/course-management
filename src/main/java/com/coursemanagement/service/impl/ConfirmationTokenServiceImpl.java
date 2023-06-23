package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.TokenStatus;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.ConfirmationTokenRepository;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.EncryptionService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final ConfirmationTokenRepository tokenRepository;
    private final EncryptionService encryptionService;
    private final UserService userService;
    private final ModelMapper mapper;
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
    public ConfirmationToken getByTokenAndType(final String token, final TokenType type) {
        return tokenRepository.findByTokenAndType(token, type)
                .map(entity -> mapper.map(entity, ConfirmationToken.class))
                .orElseThrow(() -> new SystemException("Cannot find token in the database ", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public ConfirmationToken confirmToken(final String token, final TokenType type) {
        final ConfirmationToken confirmationToken = getByTokenAndType(token, type);
        if (isTokenValid(confirmationToken)) {
            invalidateToken(confirmationToken);
            log.info("{} sent by user with id {} is confirmed", type, confirmationToken.getUserId());
            return confirmationToken;
        }
        throw new SystemException(type + " token sent by user is invalid", SystemErrorCode.BAD_REQUEST);
    }

    @Override
    @Transactional
    public UserDto confirmUserByEmailToken(final String token) {
        final String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        final ConfirmationToken confirmationToken = confirmToken(encodedToken, TokenType.EMAIL_CONFIRMATION);
        final Long userId = confirmationToken.getUserId();
        final User user = userService.getUserById(userId);
        user.setStatus(UserStatus.ACTIVE);
        return mapper.map(userService.save(user), UserDto.class);
    }

    private void invalidateToken(final ConfirmationToken token) {
        final ConfirmationTokenEntity tokenEntity = mapper.map(token, ConfirmationTokenEntity.class);
        tokenEntity.setStatus(TokenStatus.ACTIVATED);
        tokenRepository.save(tokenEntity);
    }

    private ConfirmationToken saveConfirmationToken(final User user,
                                                    final TokenType type,
                                                    final long expirationTime,
                                                    final ChronoUnit expirationTimeUnit) {
        invalidateOldTokens(user, type);
        final ConfirmationToken newToken = new ConfirmationToken()
                .setUserId(user.getId())
                .setType(type)
                .setToken(getEncryptedToken(user))
                .setStatus(TokenStatus.NOT_ACTIVATED)
                .setExpirationDate(LocalDateTime.now(DEFAULT_ZONE_ID).plus(expirationTime, expirationTimeUnit));
        final ConfirmationTokenEntity confirmationTokenEntity = mapper.map(newToken, ConfirmationTokenEntity.class);
        final ConfirmationTokenEntity savedToken = tokenRepository.save(confirmationTokenEntity);
        return mapper.map(savedToken, ConfirmationToken.class);
    }

    private void invalidateOldTokens(final User user, final TokenType tokenType) {
        var oldTokens = tokenRepository.findAllByUserIdAndType(user.getId(), tokenType);
        oldTokens.forEach(token -> token.setStatus(TokenStatus.ACTIVATED));
        tokenRepository.saveAll(oldTokens);
    }

    private String getEncryptedToken(final User user) {
        final String plainToken = String.format("%s%s", user.getEmail(), LocalDateTime.now(DEFAULT_ZONE_ID));
        return encryptionService.encryptUrlToken(plainToken);
    }

    private static boolean isTokenValid(final ConfirmationToken token) {
        return token.getExpirationDate().isAfter(LocalDateTime.now(DEFAULT_ZONE_ID))
                && Objects.equals(token.getStatus(), TokenStatus.NOT_ACTIVATED);
    }
}
