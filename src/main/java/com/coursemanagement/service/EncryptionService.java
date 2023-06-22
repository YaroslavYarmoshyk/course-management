package com.coursemanagement.service;

public interface EncryptionService {

    String encryptUrlToken(final String string);

    String decryptUrlToken(final String string);
}
