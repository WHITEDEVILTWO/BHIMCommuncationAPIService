package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TokenManager {

    private final RegenerateTokenService regenerateTokenService;
    private final RedisService redisService;

    public TokenManager(RegenerateTokenService regenerateTokenService, RedisService redisService) {
        this.regenerateTokenService = regenerateTokenService;
        this.redisService = redisService;
    }

    public Mono<String> getToken() {
        return regenerateTokenService.regenerateToken(new Registration());
//        return CompletableFuture.completedFuture(token);
    }


    public Mono<String> getRCSToken(String username, String password) {
        System.out.println("calling regenerateToken Method.........");
        Registration request = new Registration();
        request.setUsername(username);
        request.setPassword(password);
        return regenerateTokenService.regenerateToken(request);

    }

    /**
     * Returns a valid token:
     * 1. Access token if exists.
     * 2. Refresh token if access token missing.
     * 3. Regenerate access token in background if needed.
     */
    public Mono<String> getValidToken(String keyId, String key) {
        return redisService.get("RCS_access_token")
                .map(Object::toString)
                .switchIfEmpty(
                        // Access token not found
                        Mono.defer(() -> redisService.get("RCS_refresh_token")
                                .map(Object::toString)
                                .doOnNext(refresh -> log.info("⚠️ Using refresh token temporarily: {}", refresh))
                                .switchIfEmpty(
                                        // Refresh token also not found → regenerate
                                        regenerateAccessToken(keyId, key)
                                )
                        )
                );
    }

    /**
     * Regenerates access token in background and returns a temporary Mono<String>.
     */
    private Mono<String> regenerateAccessToken(String keyId, String key) {
        Registration request = new Registration();
        request.setUsername(keyId);
        request.setPassword(key);

        // Trigger regeneration in background
        regenerateTokenService.regenerateToken(request)
                .doOnNext(resp -> log.info("✅ Access token regenerated in background"))
                .subscribe(); // Non-blocking fire-and-forget

        // Return empty so that caller can use refresh token temporarily
        return Mono.empty();
    }



}
