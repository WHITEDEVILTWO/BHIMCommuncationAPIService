package org.npci.bhim.BHIMSMSserviceAPI.config;

import org.npci.bhim.BHIMSMSserviceAPI.utils.EncryptionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.crypto.SecretKey;

@Configuration
public class EncryptionConfig {

    private final EncryptionUtilities encryptionUtilities;

    private final SecretKey secretKey;

    @Value("${npci.secret.key}")
    private String base64Key;

    public EncryptionConfig(EncryptionUtilities encryptionUtilities, SecretKey secretKey) {
        this.encryptionUtilities = encryptionUtilities;
        this.secretKey = secretKey;
    }

    @Bean
    @Primary
    public SecretKey secretKey() throws Exception {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalArgumentException("npci.secret.key must be set and valid Base64 AES key");
        }
        return encryptionUtilities.secretKeyFromBase64(base64Key);
    }
}
