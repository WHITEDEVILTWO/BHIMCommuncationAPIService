package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Component
@Slf4j
public class TokenManager {

    private final RegenerateTokenService regenerateTokenService;
    private final RedisService redisService;

    public TokenManager(RegenerateTokenService regenerateTokenService, RedisService redisService) {
        this.regenerateTokenService = regenerateTokenService;
        this.redisService = redisService;
    }

//    public Mono<String> getToken() {
//        return regenerateTokenService.regenerateToken(new Registration());

    /// /        return CompletableFuture.completedFuture(token);
//    }


//    public Mono<String> getRCSToken(String username, String password) {
//        System.out.println("calling regenerateToken Method.........");
//        Registration request = new Registration();
//        request.setUsername(username);
//        request.setPassword(password);
//        return regenerateTokenService.regenerateToken(request);
//
//    }

    /**
     * Returns a valid token:
     * 1. Access token if exists.
     * 2. Refresh token if access token missing.
     * 3. Regenerate access token in background if needed.
     */
    public Mono<String> getValidToken(String keyId, String key, String Request_Channel_key) {
        return redisService.get(Request_Channel_key)
                .map(Object::toString)
                .switchIfEmpty(

                        // Access token not found -->regenerate
                        regenerateAccessToken(keyId, key, Request_Channel_key).map(Objects::toString)
                                .doOnNext(refresh -> log.info("⚠️ Using newly generated token"))
//                        Mono.defer(() -> redisService.get("RCS_refresh_token")
//                                .map(Object::toString)
//                                .doOnNext(refresh -> log.info("⚠️ Using refresh token temporarily: {}", refresh))
//                                .switchIfEmpty(regenerateAccessToken(keyId, key))
//                        )
                );
    }

    /**
     * Regenerates access token immediately and returns it.
     */
    public Mono<String> regenerateAccessToken(String keyId, String key, String Request_Channel_key) {
        Registration request = new Registration();
        request.setUsername(keyId);
        request.setPassword(key);
        Mono<String> token = redisService.get(Request_Channel_key).map(Object::toString);
        return token
                .switchIfEmpty(
                        // Save regenerated token to Redis before returning
                        regenerateTokenService.regenerateToken(request)
                                .flatMap(resp -> {
                                    log.info("✅ Access token regenerated immediately-> Status:: true");
                                    //returning geerated token
                                    return redisService.get(Request_Channel_key).map(Object::toString);
                                })
                                .switchIfEmpty(
                                        Mono.error(new RuntimeException("❌ Failed to regenerate access token"))
                                )
                );
    }
}
