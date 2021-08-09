package com.panyukovnn.instaloader.service;

import lombok.Getter;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Util to encrypt/decrypt passwords
 */
@Service
public class EncryptionUtil {

    @Value("${jasypt.encryptor.password}")
    private String encryptionPassword;

    @Getter
    private final AES256TextEncryptor textEncryptor = new AES256TextEncryptor();

    @PostConstruct
    private void postConstruct() {
        textEncryptor.setPassword(encryptionPassword);
    }
}
