package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;
import org.npci.bhim.BHIMSMSserviceAPI.entities.MediaResponseEntity;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.MediaUploadRequest;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.TextMsgRequest;
import org.npci.bhim.BHIMSMSserviceAPI.repos.MediaResponseRepository;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.MediaUploadResponse;
import org.npci.bhim.BHIMSMSserviceAPI.utils.MediaUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final WebClient webClient;
    private final RedisService redisService;

    private final TokenManager tokenManager;

//    private final MediaResponseRepository mediaResponseRepository;

    public Mono<Map<String, Object>> sendMessage(TextMsgRequest request) {
        String token = redisService.get("WA_access_token").block().toString();
        if (token == null) {
            tokenManager.getToken();
            token = redisService.get("WA_refesh_token").block().toString();
        }
        return webClient.post()
                .uri("https://api.aclwhatsapp.com/pull-platform-receiver/v2/wa/messages")
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
//                                .flatMap()
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

//    public MediaUploadResponse uploadMedia(String requestUrl) {
//        String token= redisService.get("WA_access_token").block().toString();
//        if(token == null){
//            tokenManager.getToken();
//            token= redisService.get("WA_refesh_token").block().toString();
//        }
//        String URL=String.format("https://api.aclwhatsapp.com/access-api/api/v1/wa/{}/media/upload", ConvAPIConstants.WATempltes.WBAID);
//        log.info(URL);
//        return webClient.post()
//                .uri("")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
//                .bodyValue(requestUrl)

    /// /                .exchangeToMono(clientResponse -> {
    /// /                    System.out.println("Http Status code: "+clientResponse.statusCode().value());
    /// /                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
    /// /                            })
    /// /                            .doOnNext(body -> System.out.println("Response Body : "+body))
    /// /                            .map(body->ResponseEntity.status(clientResponse.statusCode()).body(body));
    /// /
    /// /                });
//                ;
//    }
    @Transactional
    public MediaUploadResponse sendMediaRequest(MediaUploadRequest requestDTO) {

        Object token = redisService.get("WA_access_token").block();
        if (token == null) {
            tokenManager.getToken();
          token = redisService.get("WA_refesh_token").block();
        }
        // Step 1: Auto-generate format



        String URL = String.format("https://api.aclwhatsapp.com/access-api/api/v1/wa/%s/media/upload", ConvAPIConstants.WATempltes.WBAID);
        log.info(URL);
        // Step 2: Call 3rd party API
        MediaUploadResponse responseDTO = webClient.post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(MediaUploadResponse.class)
                .block(); // synchronous

        if (responseDTO != null && responseDTO.getAcknowledgementId() != null) {
            // Step 3: Save in Redis
            redisService.save(responseDTO.getAcknowledgementId(), requestDTO.getMediaUrl());

            // Step 4: Save in YugabyteDB
            MediaResponseEntity entity=new MediaResponseEntity();
            entity.setAcknowledgementId(responseDTO.getAcknowledgementId());
            entity.setMediaUrl(requestDTO.getMediaUrl());

//            mediaResponseRepository.save(entity);

        }

        return responseDTO;
    }

}
