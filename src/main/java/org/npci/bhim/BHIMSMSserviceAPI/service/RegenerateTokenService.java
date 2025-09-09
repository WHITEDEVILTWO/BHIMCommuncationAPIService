package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
public class RegenerateTokenService {

    private final WebClient webClient;
    private final RedisService redisService;

    public RegenerateTokenService(WebClient webClient, RedisService redisService) {
        this.webClient = webClient;
        this.redisService = redisService;
    }

    public Mono<String> regenerateToken(Registration request) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", request.getGrant_type());
        formData.add("client_id", request.getClient_id());
        formData.add("username", request.getUsername());
        formData.add("password", request.getPassword());

        final String PROD_URL = "https://auth.aclwhatsapp.com/realms/ipmessaging/protocol/openid-connect/token";

        return webClient.post()
                .uri(PROD_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .bodyValue(formData)
                .exchangeToMono(clientResponse ->
                        clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .flatMap(body -> {
                                    String accessToken = (String) body.get("access_token");
                                    String refreshToken = (String) body.get("refresh_token");
                                    Integer expiresIn = (Integer) body.get("expires_in");
                                    Integer refresh_expires_in = (Integer) body.get("refresh_expires_in");

                                    if (accessToken != null && formData.toSingleValueMap().containsValue("npcibhimpd") && expiresIn != null) {
                                        return redisService
                                                .save("WA_access_token", accessToken, Duration.ofSeconds(expiresIn))
                                                .and(redisService.save("WA_refresh_token", refreshToken, Duration.ofSeconds(refresh_expires_in)))
                                                .doOnNext(success -> log.info("WA Tokens cached successfully"))
                                                .thenReturn(accessToken);

                                    } else if (accessToken != null && formData.toSingleValueMap().containsValue("bhimapp_promo") && expiresIn != null) {
                                        return redisService
                                                .save("RCS_access_token", accessToken, Duration.ofSeconds(expiresIn))
                                                .and(redisService.save("RCS_refresh_token", refreshToken, Duration.ofSeconds(refresh_expires_in)))
                                                .doOnNext(success -> log.info("RCS Tokens cached successfully"))
                                                .thenReturn(accessToken);

                                    } else {
                                        log.warn("No tokens found in response or invalid client");
                                        return Mono.just(accessToken);
                                    }
                                })
                );
    }


//    public  Mono<String> regenerateToken(Registration request) {
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//        formData.add("grant_type", request.getGrant_type());
//        formData.add("client_id", request.getClient_id());
//        formData.add("username", request.getUsername());
//        formData.add("password", request.getPassword());
//
//        final String PROD_URL = String.format("https://auth.aclwhatsapp.com/realms/ipmessaging/protocol/openid-connect/token");
////        final String UAT_URL = String.format("https://apiuat.aclwhatsapp.com/auth/realms/ipmessaging/protocol/openid-connect/token");
//        Mono<ResponseEntity<Map<String, Object>>> reponsebody = webClient.post()
//                .uri(PROD_URL)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
//                .bodyValue(formData)
//                .exchangeToMono(clientResponse -> {
//                    log.info("Http Status code: {}", clientResponse.statusCode().value());
//                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//                            })
//                            .doOnNext(body ->
//                                    log.info("Response Body :{} ", body))
//                            .flatMap(body -> {
//                                log.info("Response Body: {}", body);
//
//                                //  Extract keys
//                                String accessToken = (String) body.get("access_token");
////                                log.info("Access token is : {}",accessToken);
//                                String refreshToken = (String) body.get("refresh_token");
//                                Integer expiresIn = (Integer) body.get("expires_in");
//                                Integer refresh_expires_in=(Integer) body.get("refresh_expires_in");
//
//                                if (accessToken != null &&formData.toSingleValueMap().containsValue("npcibhimpd") && expiresIn != null ) {
//                                    // ✅ Store in Redis with TTL
//                                    return redisService
//                                            .save("WA_access_token", accessToken, Duration.ofSeconds(expiresIn))
//                                            .and(redisService.save("WA_refresh_token", refreshToken, Duration.ofSeconds(refresh_expires_in))).thenReturn(accessToken)
//                                            .doOnNext(success -> log.info("Token cached: {}", success))
//                                            .thenReturn(ResponseEntity.status(clientResponse.statusCode()).body(body));
//                                }else if(accessToken != null &&formData.toSingleValueMap().containsValue("bhimapp_promo") &&expiresIn != null ){
//                                    return redisService
//                                            .save("RCS_access_token", accessToken, Duration.ofSeconds(expiresIn))
//                                            .and(redisService.save("RCS_refresh_token", refreshToken, Duration.ofSeconds(refresh_expires_in))).thenReturn(accessToken)
//                                            .doOnNext(success -> log.info("Token cached: {}", success))
//                                            .thenReturn(ResponseEntity.status(clientResponse.statusCode()).body(body));
//                                }else {
//                                    return Mono.just(ResponseEntity.status(clientResponse.statusCode()).body(body));
//                                }
//                            });
////                           .map(body -> ResponseEntity.status(clientResponse.statusCode()).body(body));
//                });
//
//        return Mono.just("Token Refreshed");
//    }
}
