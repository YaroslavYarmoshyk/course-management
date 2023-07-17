package com.coursemanagement.service;

import com.coursemanagement.enumeration.TokenStatus;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.repository.ConfirmationTokenRepository;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import com.coursemanagement.service.impl.ConfirmationTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;
import static com.coursemanagement.util.TestUtil.STUDENT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfirmationTokenServiceImplTest {
    @InjectMocks
    private ConfirmationTokenServiceImpl confirmationTokenService;
    @Mock
    private ConfirmationTokenRepository tokenRepository;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private UserService userService;
    @Spy
    private ModelMapper mapper;

    @Captor
    private ArgumentCaptor<Set<ConfirmationTokenEntity>> tokenEntitiesCaptor;

    private static final Long EMAIL_TOKEN_EXPIRATION_HOURS = 2L;
    private static final Long RESET_PASSWORD_TOKEN_EXPIRATION_MINUTES = 90L;
    private static final Long TEST_TOKEN_EXPIRATION_SECONDS = 5L;
    private static final Long START_IDS_NUMBER = 1L;


    @BeforeEach
    void setUp() {
        confirmationTokenService = new ConfirmationTokenServiceImpl(
                EMAIL_TOKEN_EXPIRATION_HOURS,
                RESET_PASSWORD_TOKEN_EXPIRATION_MINUTES,
                tokenRepository,
                encryptionService,
                userService,
                mapper
        );
        doReturn(new ConfirmationTokenEntity()).when(tokenRepository).save(any(ConfirmationTokenEntity.class));
        when(encryptionService.encryptUrlToken(anyString())).thenReturn(UUID.randomUUID().toString());
    }

    @Test
    @Order(1)
    @DisplayName("Verify old tokens get invalidated when a new email confirmation token is created")
    void whenCreateEmailConfirmationToken_thenOldTokensShouldBeInvalidated() {
        final Set<ConfirmationTokenEntity> tokenEntities = generateOldTokenEntities(3, TokenType.EMAIL_CONFIRMATION, TokenStatus.NOT_ACTIVATED);
        when(tokenRepository.findAllByUserIdAndType(any(), any())).thenReturn(tokenEntities);

        confirmationTokenService.createEmailConfirmationToken(STUDENT);

        verifyOldTokensGetActivated();
    }

    @Test
    @Order(2)
    @DisplayName("Verify old tokens get invalidated when a new reset password confirmation token is created")
    void whenCreateResetPasswordConfirmationToken_thenOldTokensShouldBeInvalidated() {
        final Set<ConfirmationTokenEntity> tokenEntities = generateOldTokenEntities(2, TokenType.RESET_PASSWORD, TokenStatus.NOT_ACTIVATED);
        when(tokenRepository.findAllByUserIdAndType(any(), any())).thenReturn(tokenEntities);

        confirmationTokenService.createResetPasswordToken(STUDENT);

        verifyOldTokensGetActivated();
    }

    private void verifyOldTokensGetActivated() {
        verify(tokenRepository).saveAll(tokenEntitiesCaptor.capture());
        final Set<ConfirmationTokenEntity> foundTokenEntities = tokenEntitiesCaptor.getValue();
        assertTrue(foundTokenEntities.stream()
                .map(ConfirmationTokenEntity::getStatus)
                .allMatch(tokenStatus -> Objects.equals(tokenStatus, TokenStatus.ACTIVATED)));
    }

    private static Set<ConfirmationTokenEntity> generateOldTokenEntities(final int tokenCount, final TokenType tokenType, final TokenStatus tokenStatus) {
        final LocalDateTime expirationDate = LocalDateTime.now(DEFAULT_ZONE_ID).plus(TEST_TOKEN_EXPIRATION_SECONDS, ChronoUnit.SECONDS);
        return LongStream.rangeClosed(START_IDS_NUMBER, tokenCount)
                .mapToObj(i -> {
                    final ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();
                    tokenEntity.setId(i);
                    tokenEntity.setToken(UUID.randomUUID().toString());
                    tokenEntity.setType(tokenType);
                    tokenEntity.setStatus(tokenStatus);
                    tokenEntity.setExpirationDate(expirationDate);
                    tokenEntity.setUserId(i);
                    return tokenEntity;
                })
                .collect(Collectors.toSet());
    }
}