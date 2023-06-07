package com.coursemanagement.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGeneratorUtils {

    public static KeyPair generateRsaKey() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot generate key pair");
        }
    }
}
