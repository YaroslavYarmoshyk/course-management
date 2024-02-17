package com.coursemanagement.unit.service;

import com.coursemanagement.exception.SystemException;
import com.coursemanagement.service.impl.EncryptionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@ExtendWith(value = MockitoExtension.class)
class EncryptionServiceImplTest {
    @Spy
    private EncryptionServiceImpl encryptionService;
    public static final String TOKEN_TO_ENCRYPT = "tokenToBeEncrypted";
    public static final String ENCRYPTED_TOKEN = "7W5qWApFTN360cF4vmiiLnhGotfEOgm3DRGcaryifP4ke65vLCOyEvOatkYfriCO";
    private static final String VALID_ENCRYPTION_KEY = "strong-$key$1991";
    private static final String INVALID_ENCRYPTION_KEY = "strong-$key";

    @TestFactory
    @DisplayName("Test encryption flow")
    Stream<DynamicTest> testUrlTokenEncryption() {
        ReflectionTestUtils.setField(encryptionService, "encryptionKey", VALID_ENCRYPTION_KEY);
        final String encryptedToken = encryptionService.encryptUrlToken(TOKEN_TO_ENCRYPT);
        final String decryptedToken = encryptionService.decryptUrlToken(encryptedToken);
        return Stream.of(
                dynamicTest("Test token encryption", () -> assertEquals(decryptedToken, TOKEN_TO_ENCRYPT)),
                dynamicTest("Test throwing exception while token encryption",
                        () -> testThrowingException(encryptionService -> encryptionService.encryptUrlToken(TOKEN_TO_ENCRYPT), "Unable to encrypt string")),
                dynamicTest("Test throwing exception while token decryption",
                        () -> testThrowingException(encryptionService -> encryptionService.decryptUrlToken(ENCRYPTED_TOKEN), "Unable to decrypt string"))
        );
    }

    void testThrowingException(final Consumer<EncryptionServiceImpl> encryptionServiceConsumer, final String expectedMessage) {
        ReflectionTestUtils.setField(encryptionService, "encryptionKey", INVALID_ENCRYPTION_KEY);
        assertThrowsWithMessage(
                () -> encryptionServiceConsumer.accept(encryptionService),
                SystemException.class,
                expectedMessage
        );

    }
}