/**
 * Repalle Ganesh Babu*/
package org.npci.bhim.BHIMSMSserviceAPI.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;
import org.npci.bhim.BHIMSMSserviceAPI.entities.MediaResponseEntity;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.MediaUploadRequest;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.WaTextMsgRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final WebClient webClient;
    private final RedisService redisService;

    private final TokenManager tokenManager;

    public static final String Request_Channel_key="WA_access_token";

    @Value("${npci.wa.uname}")
    String keyId;
    @Value("${npci.wa.key}")
    String key;

//    private final MediaResponseRepository mediaResponseRepository;

    public Mono<Map<String, Object>> sendMessage(WaTextMsgRequest request) throws JsonProcessingException {
//        log.info("Rcs Template Message Request------>\n,{}",request);
        ObjectMapper mapper=new ObjectMapper();
        mapper.setSerializationInclusion((JsonInclude.Include.NON_NULL));
        String json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
        AtomicInteger count= new AtomicInteger();

//        log.info("Outgoing RCS Template Message Request----> \n: {}",json);
        return tokenManager.getValidToken(keyId, key,Request_Channel_key)
                .flatMap(accessToken -> sendMessageWithToken(request, accessToken))
                .onErrorResume(ex -> {
                    // If token expired (403), regenerate and retry once
                    if (ex.getMessage()!=null && ex.getMessage().contains("403")) {
                        log.info("Count of Failed messages for WA text Messages: {}",count.getAndIncrement());
                        log.warn("!Access token might be expired or not authorized, regenerating and retrying...");
                        return tokenManager.regenerateAccessToken(keyId, key,Request_Channel_key)
                                .flatMap(newToken -> sendMessageWithToken(request, newToken));
                    }
                    return Mono.error(ex); // propagate other errors
                });
    }
    public Mono<Map<String, Object>> sendMessageWithToken(WaTextMsgRequest request,String token) {

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
                                .doOnNext(body->
                                        log.info("✅ Message sent successfully. Status: {}, Response: {}", status.value(), body))
                                .doOnNext(body-> {
                                    ObjectMapper mapper=new ObjectMapper();
                                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                                    try {
                                        String  json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
                                        String responseId = (String) body.get("responseId");
                                        log.info("ResponseId: {}",responseId);
                                        log.info("Saving response and body to redis\n: {}",json);
                                        redisService.save(responseId, json)
                                                .doOnNext(saved -> log.info("✅ Saved to Redis: {}", saved))
                                                .doOnError(err -> log.error("❌ Failed to save to Redis", err))
                                                .subscribe();
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException("unable to save to redis / json parsing exception. ",e);
                                    }
                                });
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
    //-------------------------------------------------------------------------------------------------------
    public Mono<Map<String, Object>> sendMediaRequest(MediaUploadRequest request) throws JsonProcessingException {
//        log.info("Rcs Template Message Request------>\n,{}",request);
        ObjectMapper mapper=new ObjectMapper();

        mapper.setSerializationInclusion((JsonInclude.Include.NON_NULL));

        String json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);

//        log.info("Outgoing RCS Template Message Request----> \n: {}",json);
        return tokenManager.getValidToken(keyId, key,Request_Channel_key)
                .flatMap(accessToken -> sendMessageWithToken(request, accessToken))
                .onErrorResume(ex -> {
                    // If token expired (403), regenerate and retry once
                    if (ex.getMessage()!=null && ex.getMessage().contains("403")) {
                        log.warn("!Access token might be expired or not authorized, regenerating and retrying...");
                        return tokenManager.regenerateAccessToken(keyId, key,Request_Channel_key)
                                .flatMap(newToken -> sendMessageWithToken(request, newToken));
                    }
                    return Mono.error(ex); // propagate other errors
                });
    }


    @Transactional
    public Mono<Map<String, Object>> sendMessageWithToken(MediaUploadRequest requestDTO, String token) {

        String URL = String.format("https://api.aclwhatsapp.com/access-api/api/v1/wa/%s/media/upload",
                ConvAPIConstants.WATempltes.WBAID);
        log.info("Calling Media Upload API: {}", URL);

        return webClient.post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .flatMap(responseMap -> {
                    log.info("✅ Media Upload Response: {}", responseMap);

                    // Extract acknowledgementId from response
                    String acknowledgementId = (String) responseMap.get("acknowledgementId");

                    if (acknowledgementId != null) {
                        // Save in Redis (fire & forget)
                        redisService.save(acknowledgementId, requestDTO.getMediaUrl())
                                .doOnNext(saved -> log.info("✅ Saved to Redis with ackId={} -> {}", acknowledgementId, requestDTO.getMediaUrl()))
                                .doOnError(err -> log.error("❌ Failed to save to Redis", err))
                                .subscribe();

                        // Save in Yugabyte (fire & forget)
                        MediaResponseEntity entity = new MediaResponseEntity();
                        entity.setAcknowledgementId(acknowledgementId);
                        entity.setMediaUrl(requestDTO.getMediaUrl());

                        // Uncomment when repo is ready
                        // Mono.fromRunnable(() -> mediaResponseRepository.save(entity))
                        //         .subscribe();
                    }

                    return Mono.just(responseMap); // ✅ return response JSON
                });
    }

//    @Transactional
//    public Mono<Map<String, Object>> sendMessageWithToken(MediaUploadRequest requestDTO,String token) {
//
//        // Step 1: Auto-generate format
//        String URL = String.format("https://api.aclwhatsapp.com/access-api/api/v1/wa/%s/media/upload", ConvAPIConstants.WATempltes.WBAID);
//        log.info(URL);
//        // Step 2: Call 3rd party API
//        MediaUploadResponse responseDTO = webClient.post()
//                .uri(URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
//                .bodyValue(requestDTO)
//                .retrieve()
//                .bodyToMono(MediaUploadResponse.class)
//                .block(); // synchronous
//
//        if (responseDTO != null && responseDTO.getAcknowledgementId() != null) {
//            // Step 3: Save in Redis
//            redisService.save(responseDTO.getAcknowledgementId(), requestDTO.getMediaUrl());
//
//            // Step 4: Save in YugabyteDB
//            MediaResponseEntity entity=new MediaResponseEntity();
//            entity.setAcknowledgementId(responseDTO.getAcknowledgementId());
//            entity.setMediaUrl(requestDTO.getMediaUrl());
//
////            mediaResponseRepository.save(entity);
//
//        }
//
//        return;
//    }

}
