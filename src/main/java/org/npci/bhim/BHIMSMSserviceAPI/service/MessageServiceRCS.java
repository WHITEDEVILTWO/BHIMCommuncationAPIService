package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.rcsMessageRequests.RCSTemplateMessageRequest;
import org.npci.bhim.BHIMSMSserviceAPI.rcsMessageRequests.RCSTextMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceRCS {

    private final WebClient webClient;
    private final RedisService redisService;

    private final TokenManager tokenManager;
    @Value("${npci.rcs.uname}")
    String keyId;
    @Value("${npci.rcs.key}")
    String key;

    public Mono<Map<String, Object>> sendMessage(RCSTemplateMessageRequest request) {
        return tokenManager.getValidToken(keyId, key)
                .flatMap(accessToken -> sendMessageWithToken(request, accessToken))
                .onErrorResume(ex -> {
                    // If token expired (403), regenerate and retry once
                    if (ex.getMessage() != null && ex.getMessage().contains("403")) {
                        log.warn("Access token might be expired, regenerating and retrying...");
                        return tokenManager.getValidToken(keyId, key)
                                .flatMap(newToken -> sendMessageWithToken(request, newToken));
                    }
                    return Mono.error(ex); // propagate other errors
                });
    }

    private Mono<Map<String, Object>> sendMessageWithToken(RCSTemplateMessageRequest request, String token) {
        log.info("Sending message using access token: {}", token);
        String URL_RCS = "https://convapi.aclwhatsapp.com/v1/projects/e3fe594a-90d9-4387-9848-b1ceca763d87/messages:send";
        return webClient.post()
                .uri(URL_RCS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(request)
                .exchangeToMono(clientResponse -> {
                    HttpStatus status = (HttpStatus) clientResponse.statusCode();
                    MediaType contentType = clientResponse.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

                    if (status.is2xxSuccessful() && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                        return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
                    } else if (status.value() == 403) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("403 Forbidden: {}", body);
                                    return Mono.error(new RuntimeException("403 FORBIDDEN"));
                                });
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Message send failed. Status: {}, Body: {}", status.value(), body);
                                    return Mono.error(new RuntimeException("Failed to send message: " + status + " - " + body));
                                });
                    }
                });
    }



//
    public Mono<Map<String, Object>> sendMessaage(RCSTextMessageRequest request) {
        String URL_RCS = "https://convapi.aclwhatsapp.com/v1/projects/e3fe594a-90d9-4387-9848-b1ceca763d87/messages:send";

        return tokenManager.getValidToken(keyId, key)
                .flatMap(token -> {
                    log.info("Access token to use: {}", token);

                    return webClient.post()
                            .uri(URL_RCS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .bodyValue(request)
//                .exchangeToMono(clientResponse -> {
//                    System.out.println("Http Status code: "+clientResponse.statusCode().value());
//                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//                            })
//                            .doOnNext(body -> System.out.println("Response Body : "+body))
//                            .map(body->ResponseEntity.status(clientResponse.statusCode()).body(body));
//
//                });
                            .exchangeToMono(clientResponse -> {
                                HttpStatus status = (HttpStatus) clientResponse.statusCode();
                                System.out.println("HTTP Status code: " + status.value());

                                MediaType contentType = clientResponse.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

                                // If response is JSON, parse it into Map
                                if (status.is2xxSuccessful() && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                                            })
                                            .doOnNext(body -> System.out.println("Response Body: " + body));
                                } else {
                                    // Otherwise, read it as plain String and log for debugging
                                    return clientResponse.bodyToMono(String.class)
                                            .flatMap(body -> {
                                                System.err.println("Non-JSON response body: " + body);
                                                return Mono.error(new RuntimeException("Unexpected response type or error: " + status));
                                            });
                                }
                            });
                })
                .doOnError(e -> log.error("Error while sending message: {}", e.getMessage(), e))
                .doOnSuccess(resp -> log.info("Final Response returned to caller: {}", resp)); // <--- extra log

////        String URL_RCS = String.format("https://convapi.aclwhatsapp.com/v1/projects/e3fe594a-90d9-4387-9848-b1ceca763d87/messages:send");
////        return webClient.post()
////                .uri(URL_RCS)
////                .contentType(MediaType.APPLICATION_JSON)
////                .accept(MediaType.APPLICATION_JSON)
////                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
////                .bodyValue(request)
//////                .exchangeToMono(clientResponse -> {
//////                    System.out.println("Http Status code: "+clientResponse.statusCode().value());
//////                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//////                            })
//////                            .doOnNext(body -> System.out.println("Response Body : "+body))
//////                            .map(body->ResponseEntity.status(clientResponse.statusCode()).body(body));
//////
//////                });
////                .exchangeToMono(clientResponse -> {
////                    HttpStatus status = (HttpStatus) clientResponse.statusCode();
////                    System.out.println("HTTP Status code: " + status.value());
////
////                    MediaType contentType = clientResponse.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
////
////                    // If response is JSON, parse it into Map
////                    if (status.is2xxSuccessful() && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
////                        return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
////                                })
////                                .doOnNext(body -> System.out.println("Response Body: " + body));
////                    } else {
////                        // Otherwise, read it as plain String and log for debugging
////                        return clientResponse.bodyToMono(String.class)
////                                .flatMap(body -> {
////                                    System.err.println("Non-JSON response body: " + body);
////                                    return Mono.error(new RuntimeException("Unexpected response type or error: " + status));
////                                });
////                    }
////                });
    }
//
//    public Mono<Map<String, Object>> sendMessaage(RCSTemplateMessageRequest request) {
//        String URL_RCS = "https://convapi.aclwhatsapp.com/v1/projects/e3fe594a-90d9-4387-9848-b1ceca763d87/messages:send";
//
//        return tokenManager.getValidToken(keyId, key)
//                .flatMap(token -> {
//                    log.info("Access token to use: {}", token);
//
//                    return webClient.post()
//                            .uri(URL_RCS)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .accept(MediaType.APPLICATION_JSON)
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                            .bodyValue(request)
////                .exchangeToMono(clientResponse -> {
////                    System.out.println("Http Status code: "+clientResponse.statusCode().value());
////                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
////                            })
////                            .doOnNext(body -> System.out.println("Response Body : "+body))
////                            .map(body->ResponseEntity.status(clientResponse.statusCode()).body(body));
////
////                });
//                            .exchangeToMono(clientResponse -> {
//                                HttpStatus status = (HttpStatus) clientResponse.statusCode();
//                                System.out.println("HTTP Status code: " + status.value());
//
//                                MediaType contentType = clientResponse.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
//
//                                // If response is JSON, parse it into Map
//                                if (status.is2xxSuccessful() && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
//                                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//                                            })
//                                            .doOnNext(body -> System.out.println("Response Body: " + body));
//                                } else {
//                                    // Otherwise, read it as plain String and log for debugging
//                                    return clientResponse.bodyToMono(String.class)
//                                            .flatMap(body -> {
//                                                System.err.println("Non-JSON response body: " + body);
//                                                return Mono.error(new RuntimeException("Unexpected response type or error: " + status));
//                                            });
//                                }
//                            });
//                })
//                .doOnError(e -> log.error("Error while sending message: {}", e.getMessage(), e))
//                .doOnSuccess(resp -> log.info("Final Response returned to caller: {}", resp)); // <--- extra log
//    }
//
////    public Mono<Map<String, Object>> sendMessaage(RCSTemplateMessageRequest request) {
////        String accessTokentoken = String.valueOf(redisService.get("RCS_access_token"));
////        String token=accessTokentoken.toString();
////        log.info("Access toke-----------------------------> ",token);
////        if (token == null) {
////            String resp= String.valueOf(tokenManager.getRCSToken(keyId, key));
////            log.info(resp);
////            if ((redisService.get("RCS_refresh_token")) != null) {
////                token = redisService.get("RCS_refresh_token").toString();
////                log.info(token);
////            }
////        }
////
////        String URL_RCS = String.format("https://convapi.aclwhatsapp.com/v1/projects/e3fe594a-90d9-4387-9848-b1ceca763d87/messages:send");
////        return webClient.post()
////                .uri(URL_RCS)
////                .contentType(MediaType.APPLICATION_JSON)
////                .accept(MediaType.APPLICATION_JSON)
////                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
////                .bodyValue(request)
//////                .exchangeToMono(clientResponse -> {
//////                    System.out.println("Http Status code: "+clientResponse.statusCode().value());
//////                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//////                            })
//////                            .doOnNext(body -> System.out.println("Response Body : "+body))
//////                            .map(body->ResponseEntity.status(clientResponse.statusCode()).body(body));
//////
//////                });
////                .exchangeToMono(clientResponse -> {
////                    HttpStatus status = (HttpStatus) clientResponse.statusCode();
////                    System.out.println("HTTP Status code: " + status.value());
////                    log.info("Access key from ------>: ",accessTokentoken.toString());
////
////                    MediaType contentType = clientResponse.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
////
////                    // If response is JSON, parse it into Map
////                    if (status.is2xxSuccessful() && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
////                        return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
////                                })
////                                .doOnNext(body -> System.out.println("Response Body: " + body));
////                    } else {
////                        // Otherwise, read it as plain String and log for debugging
////                        return clientResponse.bodyToMono(String.class)
////                                .flatMap(body -> {
////                                    System.err.println("Non-JSON response body: " + body);
////                                    return Mono.error(new RuntimeException("Unexpected response type or error: " + status));
////                                });
////                    }
////                });
////    }


}
