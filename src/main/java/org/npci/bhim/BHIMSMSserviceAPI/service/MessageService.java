package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.RequiredArgsConstructor;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;
import org.npci.bhim.BHIMSMSserviceAPI.model.TextMsgRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    @Autowired
    private WebClient webClient;

    public Mono<Map<String, Object>> sendMessaage(TextMsgRequest request) {
        String token= ConvAPIConstants.token;
        return webClient.post()
                .uri("https://api.aclwhatsapp.com/pull-platform-receiver/v2/wa/messages")
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
