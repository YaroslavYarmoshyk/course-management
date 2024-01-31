package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.service.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

@Slf4j
@Service
public class EncryptionServiceImpl implements EncryptionService {
    private static final String AES = "AES";
    private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    private static final String SUN_JSE = "SunJCE";
    private static final int IV_SIZE = 16;
    @Value("${encryption.key:strong-$key$1991}")
    private String encryptionKey;

    @Override
    public String encryptUrlToken(final String string) {
        try {
            final Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING, SUN_JSE);
            final SecretKeySpec key = new SecretKeySpec(getBytes(encryptionKey), AES);

            final byte[] iv = generateIv();
            cipher.init(ENCRYPT_MODE, key, new IvParameterSpec(iv));

            final byte[] encrypted = cipher.doFinal(getBytes(string));
            final byte[] joinedBytes = joinByteArrays(encrypted, iv);

            final String encryptedString = Base64.getEncoder().encodeToString(joinedBytes);

            return URLEncoder.encode(encryptedString, StandardCharsets.UTF_8);
        } catch (final GeneralSecurityException e) {
            throw new SystemException("Unable to encrypt string", SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String decryptUrlToken(final String string) {
        try {
            final Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING, SUN_JSE);
            final SecretKeySpec key = new SecretKeySpec(getBytes(encryptionKey), AES);

            final String nonUrlSafeString = URLDecoder.decode(string, StandardCharsets.UTF_8);
            final byte[] decodedBase64 = Base64.getDecoder().decode(nonUrlSafeString);

            cipher.init(DECRYPT_MODE, key, new IvParameterSpec(getIvFromJoinByteArray(decodedBase64)));

            return new String(cipher.doFinal(getCipherFromJoinByteArray(decodedBase64)), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new SystemException("Unable to decrypt string", SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static byte[] getBytes(final String encryptKey) {
        return encryptKey.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] joinByteArrays(byte[] cipher, byte[] iv) {
        return ByteBuffer.allocate(cipher.length + iv.length)
                .put(cipher)
                .put(iv)
                .array();
    }

    private byte[] getCipherFromJoinByteArray(byte[] byteArray) {
        return Arrays.copyOfRange(byteArray, 0, byteArray.length - IV_SIZE);
    }

    private byte[] getIvFromJoinByteArray(byte[] byteArray) {
        return Arrays.copyOfRange(byteArray, byteArray.length - IV_SIZE, byteArray.length);
    }
}
