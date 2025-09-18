package org.npci.bhim.BHIMSMSserviceAPI.config;

import org.npci.bhim.BHIMSMSserviceAPI.utils.EncryptionUtilities;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Configuration
public class CryptoConfig {

    private final EncryptionUtilities encryptionUtilities;

    @Value("${npci.secret.key}")
    private String base64Key;

    public CryptoConfig(EncryptionUtilities encryptionUtilities) {
        this.encryptionUtilities = encryptionUtilities;
    }

    @Bean(name = "SecondarySecretKeyBean")
    public SecretKey secretKey() throws Exception {
//        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//        keyGen.init(128); // or 128 depending on your requirement
//        return keyGen.generateKey();
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalArgumentException("npci.secret.key must be set and valid Base64 AES key");
        }
        return encryptionUtilities.secretKeyFromBase64(base64Key);
    }
}
