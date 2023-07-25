package com.coursemanagement.service;

import com.coursemanagement.config.extension.UserProviderExtension;
import com.coursemanagement.enumeration.TokenStatus;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.ConfirmationTokenRepository;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import com.coursemanagement.service.impl.ConfirmationTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.DateTimeUtils.DEFAULT_ZONE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfirmationTokenServiceImplTest {
    @InjectMocks
    @Spy
    private ConfirmationTokenServiceImpl confirmationTokenService;
    @Mock
    private ConfirmationTokenRepository tokenRepository;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private UserService userService;
    @Spy
    private ModelMapper mapper;

    @RegisterExtension
    private static final UserProviderExtension USER_PROVIDER = new UserProviderExtension();
    @Captor
    private ArgumentCaptor<Set<ConfirmationTokenEntity>> tokenEntitiesCaptor;

    private static final Long EMAIL_TOKEN_EXPIRATION_HOURS = 2L;
    private static final Long RESET_PASSWORD_TOKEN_EXPIRATION_MINUTES = 90L;
    private static final Long TEST_TOKEN_EXPIRATION_SECONDS = 5L;
    private static final Long START_IDS_NUMBER = 1L;
    private static final int RANDOM_OLD_TOKEN_COUNT_LIMIT = 5;
    private static final String TOKEN_VALUE = UUID.randomUUID().toString();
    private static final User STUDENT = USER_PROVIDER.getStudent();

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
        confirmationTokenService = spy(confirmationTokenService);
    }

    @Nested
    @DisplayName("Old tokens invalidation")
    class OldTokenInvalidationTests {

        @ParameterizedTest(name = "{index} old tokens are invalidated after {0} is created")
        @MethodSource("provideTokenTypesAndServiceMethods")
        @DisplayName("Verify old tokens get invalidated when a new confirmation token is created")
        void testOldTokensInvalidation(TokenType tokenType, Consumer<ConfirmationTokenService> tokenCreator) {
            final Set<ConfirmationTokenEntity> tokenEntities = generateTokenEntities(tokenType);
            when(tokenRepository.findAllByUserIdAndType(any(), any())).thenReturn(tokenEntities);
            when(encryptionService.encryptUrlToken(anyString())).thenReturn(TOKEN_VALUE);
            doReturn(new ConfirmationTokenEntity()).when(tokenRepository).save(any(ConfirmationTokenEntity.class));

            tokenCreator.accept(confirmationTokenService);
            verify(tokenRepository).saveAll(tokenEntitiesCaptor.capture());

            final Set<ConfirmationTokenEntity> foundTokenEntities = tokenEntitiesCaptor.getValue();
            assertTrue(foundTokenEntities.stream()
                    .map(ConfirmationTokenEntity::getStatus)
                    .allMatch(tokenStatus -> Objects.equals(tokenStatus, TokenStatus.ACTIVATED)));
        }

        private static Stream<Arguments> provideTokenTypesAndServiceMethods() {
            return Stream.of(
                    Arguments.of(TokenType.EMAIL_CONFIRMATION, (Consumer<ConfirmationTokenService>) service -> service.createEmailConfirmationToken(STUDENT)),
                    Arguments.of(TokenType.RESET_PASSWORD, (Consumer<ConfirmationTokenService>) service -> service.createResetPasswordToken(STUDENT))
            );
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Token confirmation")
    class TokenConfirmationTests {

        @Order(1)
        @ParameterizedTest(name = "{index} token type = {0}")
        @EnumSource
        @DisplayName("Test token confirmation by token and type when token exists")
        void testFindConfirmationTokenByTokenAndTypeExist(TokenType tokenType) {
            final ConfirmationTokenEntity confirmationTokenEntity = getConfirmationTokenEntity(tokenType, TokenStatus.NOT_ACTIVATED);
            when(tokenRepository.findByTokenAndType(any(), any())).thenReturn(Optional.of(confirmationTokenEntity));

            final var actualToken = confirmationTokenService.confirmToken(confirmationTokenEntity.getToken(), tokenType);

            assertEquals(confirmationTokenEntity.getId(), actualToken.getId());
            assertEquals(confirmationTokenEntity.getToken(), actualToken.getToken());
            assertEquals(TokenStatus.ACTIVATED, actualToken.getStatus());
        }

        @Order(2)
        @ParameterizedTest(name = "{index} token type = {0}")
        @EnumSource
        @DisplayName("Test throwing system exception when token doesn't exist")
        void testThrowSystemExceptionWhenTokenAndTypeDoNotExist(TokenType tokenType) {
            when(tokenRepository.findByTokenAndType(any(), any())).thenReturn(Optional.empty());
            assertThrowsWithMessage(
                    () -> confirmationTokenService.confirmToken(TOKEN_VALUE, tokenType),
                    SystemException.class,
                    "Cannot find token in the database"
            );
        }

        @Order(3)
        @ParameterizedTest(name = "{index} token type = {0}")
        @EnumSource
        @DisplayName("Test valid token is activated after confirmation")
        void testValidTokenIsActivated(TokenType tokenType) {
            final ConfirmationTokenEntity confirmationTokenEntity = getConfirmationTokenEntity(tokenType, TokenStatus.NOT_ACTIVATED);
            when(tokenRepository.findByTokenAndType(any(), any())).thenReturn(Optional.of(confirmationTokenEntity));

            final var actualConfirmationToken = confirmationTokenService.confirmToken(confirmationTokenEntity.getToken(), tokenType);

            assertEquals(TokenStatus.ACTIVATED, actualConfirmationToken.getStatus());
            assertEquals(1L, actualConfirmationToken.getId());
        }

        @Order(4)
        @TestFactory
        @DisplayName("Test throwing system exception during confirmation when token is invalid")
        Stream<DynamicTest> testThrowSystemExceptionWhenTokenIsInvalid() {
            final LocalDateTime expiredDate = LocalDateTime.now(DEFAULT_ZONE_ID).minusMinutes(1L);
            final Stream<ConfirmationTokenEntity> argsStream = generateInvalidTokenEntities(expiredDate);

            final Function<ConfirmationTokenEntity, String> displayName = input -> "Test token: " + input.getType()
                    + " with status: " + input.getStatus()
                    + " expired: " + Objects.equals(input.getExpirationDate(), expiredDate);

            final ThrowingConsumer<ConfirmationTokenEntity> testExecutor = this::confirmInvalidToken;

            return DynamicTest.stream(argsStream, displayName, testExecutor);
        }

        private Stream<ConfirmationTokenEntity> generateInvalidTokenEntities(final LocalDateTime expiredDate) {
            final ConfirmationTokenEntity activatedResetPassToken = getConfirmationTokenEntity(TokenType.RESET_PASSWORD, TokenStatus.ACTIVATED);
            final ConfirmationTokenEntity activatedEmailToken = getConfirmationTokenEntity(TokenType.EMAIL_CONFIRMATION, TokenStatus.ACTIVATED);
            final ConfirmationTokenEntity expiredResetPassToken = getConfirmationTokenEntity(TokenType.RESET_PASSWORD, TokenStatus.NOT_ACTIVATED);
            final ConfirmationTokenEntity expiredEmailToken = getConfirmationTokenEntity(TokenType.EMAIL_CONFIRMATION, TokenStatus.NOT_ACTIVATED);
            expiredResetPassToken.setExpirationDate(expiredDate);
            expiredEmailToken.setExpirationDate(expiredDate);

            return Stream.of(activatedResetPassToken, activatedEmailToken, expiredResetPassToken, expiredEmailToken);
        }

        private void confirmInvalidToken(final ConfirmationTokenEntity invalidTokenEntity) {
            final String token = invalidTokenEntity.getToken();
            final TokenType tokenType = invalidTokenEntity.getType();

            doReturn(Optional.of(invalidTokenEntity)).when(tokenRepository).findByTokenAndType(token, tokenType);

            assertThrowsWithMessage(
                    () -> confirmationTokenService.confirmToken(token, tokenType),
                    SystemException.class,
                    tokenType + " token sent by user is invalid"
            );
        }
    }

    @Nested
    @DisplayName("Invocation user activation")
    class InvocationUserActivationTests {

        @Test
        @DisplayName("Test invoke user activation after email confirmation")
        void testInvokeUserActivationAfterEmailConfirmation() {
            final ConfirmationTokenEntity confirmationTokenEntity = getConfirmationTokenEntity(TokenType.EMAIL_CONFIRMATION, TokenStatus.NOT_ACTIVATED);
            final String token = confirmationTokenEntity.getToken();
            final TokenType tokenType = confirmationTokenEntity.getType();
            doReturn(mapper.map(confirmationTokenEntity, ConfirmationToken.class)).when(confirmationTokenService).confirmToken(token, tokenType);

            confirmationTokenService.confirmUserByEmailToken(token);

            verify(userService).activateById(confirmationTokenEntity.getUserId());
        }
    }

    private static ConfirmationTokenEntity getConfirmationTokenEntity(final TokenType tokenType, final TokenStatus tokenStatus) {
        final ConfirmationTokenEntity confirmationTokenEntity = new ConfirmationTokenEntity();
        confirmationTokenEntity.setId(START_IDS_NUMBER);
        confirmationTokenEntity.setToken(TOKEN_VALUE);
        confirmationTokenEntity.setType(tokenType);
        confirmationTokenEntity.setStatus(tokenStatus);
        confirmationTokenEntity.setUserId(START_IDS_NUMBER);
        confirmationTokenEntity.setExpirationDate(LocalDateTime.now(DEFAULT_ZONE_ID).plusHours(EMAIL_TOKEN_EXPIRATION_HOURS));
        return confirmationTokenEntity;

    }

    private static Set<ConfirmationTokenEntity> generateTokenEntities(final TokenType tokenType) {
        final LocalDateTime expirationDate = LocalDateTime.now(DEFAULT_ZONE_ID).plus(TEST_TOKEN_EXPIRATION_SECONDS, ChronoUnit.SECONDS);
        final int randomTokenCount = new Random().nextInt(RANDOM_OLD_TOKEN_COUNT_LIMIT);
        return LongStream.rangeClosed(START_IDS_NUMBER, randomTokenCount)
                .mapToObj(i -> {
                    final ConfirmationTokenEntity tokenEntity = new ConfirmationTokenEntity();
                    tokenEntity.setId(i);
                    tokenEntity.setToken(TOKEN_VALUE);
                    tokenEntity.setType(tokenType);
                    tokenEntity.setStatus(TokenStatus.NOT_ACTIVATED);
                    tokenEntity.setExpirationDate(expirationDate);
                    tokenEntity.setUserId(i);
                    return tokenEntity;
                })
                .collect(Collectors.toSet());
    }
}
