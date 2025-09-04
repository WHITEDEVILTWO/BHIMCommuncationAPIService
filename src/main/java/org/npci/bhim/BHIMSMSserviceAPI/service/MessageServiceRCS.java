package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.modelRCS.RCSTextMessageRequest;
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

    public Mono<Map<String, Object>> sendMessaage(RCSTextMessageRequest request) {
        String token= redisService.get("RCS_access_token").block().toString();
        if(token == null){
            tokenManager.getToken();
            token= redisService.get("RCS_refesh_token").block().toString();
        }
        String URL_RCS=String.format("https://convapi.aclwhatsapp.com/v1/projects/e3fe594a-90d9-4387-9848-b1ceca763d87/messages:send");
        return webClient.post()
                .uri(URL_RCS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
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
                        return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
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
    }
}
