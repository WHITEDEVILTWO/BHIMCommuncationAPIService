package org.npci.bhim.BHIMSMSserviceAPI.service;

import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.springframework.stereotype.Component;

@Component
public class TokenManager {

    private final RegenerateTokenService regenerateTokenService;

    public TokenManager(RegenerateTokenService regenerateTokenService) {
        this.regenerateTokenService = regenerateTokenService;
    }

    public void getToken() {
        regenerateTokenService.regenerateToken(new Registration());
    }
}
