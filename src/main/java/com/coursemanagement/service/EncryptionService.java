package com.coursemanagement.service;

public interface EncryptionService {

    String encrypt(final String string);

    String decrypt(final String string);
}
