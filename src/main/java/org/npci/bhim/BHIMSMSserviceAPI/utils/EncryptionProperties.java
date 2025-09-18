package org.npci.bhim.BHIMSMSserviceAPI.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "npci.encryption")
@Getter
@Setter
public class EncryptionProperties {
    private List<String> fields; // list of dot-paths to encrypt
}
