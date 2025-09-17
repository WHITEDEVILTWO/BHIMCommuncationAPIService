/**
 * Repalle Ganesh Babu
 */
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
import org.npci.bhim.BHIMSMSserviceAPI.repoServices.MediaResponseService;
import org.npci.bhim.BHIMSMSserviceAPI.repoServices.WAResponseService;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.MediaUploadResponse;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.WAResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final WebClient webClient;
    private final RedisService redisService;

    private final TokenManager tokenManager;

    public static final String REQUEST_CHANNEL_KEY = "WA_access_token";

    private final WAResponseService waResponseService;
    private final MediaResponseService mediaResponseService;

    @Value("${npci.wa.uname}")
    String keyId;
    @Value("${npci.wa.key}")
    String key;

//    private final MediaResponseRepository mediaResponseRepository;

    private static final int MAX_RETRIES = 3;

    public Mono<Map<String, Object>> sendMessage(WaTextMsgRequest request) throws JsonProcessingException {
        return tokenManager.getValidToken(keyId, key, REQUEST_CHANNEL_KEY)
                .flatMap(token -> sendMessageWithToken(request, token))
                .timeout(Duration.ofSeconds(5)) // base timeout
                .doOnError(error -> log.error("⏳ Request timed out: {}", error.getMessage()))
                .onErrorResume(ex -> handleError(request, ex));
    }

    private Mono<Map<String, Object>> handleError(WaTextMsgRequest request, Throwable ex) {
        if (ex.getMessage() == null) return Mono.error(ex);

        if (ex instanceof java.util.concurrent.TimeoutException) {
            return retryWithCustomTimeout(request, Duration.ofSeconds(5), "Timeout occurred → retrying...");
        }
        if (ex.getMessage().contains("400")) {
            return retryOnBadRequest(request);
        }
        if (ex.getMessage().contains("401")) {
            return retryOnAuthFailure(request);
        }
        if (ex.getMessage().contains("403")) {
            return retryOnForbidden(request);
        }
        if (ex.getMessage().contains("500")) {
            return retryOnServerError(request);
        }
        return Mono.error(ex); // propagate unhandled errors
    }

    // 🔹 Retry methods with different timeouts
    private Mono<Map<String, Object>> retryOnBadRequest(WaTextMsgRequest request) {
        return retryWithCustomTimeout(request, Duration.ofSeconds(3), "400 Bad Request → Retrying...");
    }

    private Mono<Map<String, Object>> retryOnAuthFailure(WaTextMsgRequest request) {
        return retryWithCustomTimeout(request, Duration.ofSeconds(5), "401 Unauthorized → Retrying after token refresh...");
    }

    private Mono<Map<String, Object>> retryOnForbidden(WaTextMsgRequest request) {
        return retryWithCustomTimeout(request, Duration.ofSeconds(5), "403 Forbidden → Retrying with new token...");
    }

    private Mono<Map<String, Object>> retryOnServerError(WaTextMsgRequest request) {
        return retryWithCustomTimeout(request, Duration.ofSeconds(30), "500 Internal Server Error → Retrying...");
    }

    private Mono<Map<String, Object>> retryWithCustomTimeout(WaTextMsgRequest request, Duration timeout, String logMsg) {
        log.warn(logMsg);
        return tokenManager.regenerateAccessToken(keyId, key,REQUEST_CHANNEL_KEY)
                .flatMap(newToken -> sendMessageWithToken(request, newToken))
                .timeout(timeout)
                .retry(MAX_RETRIES);
    }

    // 🔹 Existing method to send WA message
    public Mono<Map<String, Object>> sendMessageWithToken(WaTextMsgRequest request, String token) {
        return webClient.post()
                .uri("https://api.aclwhatsapp.com/pull-platform-receiver/v2/wa/messages")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(request)
                .exchangeToMono(clientResponse -> {
                    HttpStatus status = (HttpStatus) clientResponse.statusCode();
                    log.info("HTTP Status code: {}", status.value());

                    MediaType contentType = clientResponse.headers()
                            .contentType()
                            .orElse(MediaType.APPLICATION_OCTET_STREAM);

                    if (status.is2xxSuccessful() && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                        return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .doOnNext(body -> handleSuccessResponse(request, body));
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("❌ Non-JSON response body: {}", body);
                                    return Mono.error(new RuntimeException("Unexpected response type or error: " + status));
                                });
                    }
                });
    }

    // 🔹 Save success response in DB + Redis
    private void handleSuccessResponse(WaTextMsgRequest request, Map<String, Object> body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(request);

            // Save in DB
            WAResponses entity = new WAResponses();
            entity.setResponseId((String) body.get("responseId"));
            entity.setRequestBody(json);
            waResponseService.saveToDb(entity);

            // Save in Redis
            String responseId = (String) body.get("responseId");
            redisService.save(responseId, json).subscribe();

            log.info("✅ Response {} saved in DB and Redis", responseId);
        } catch (JsonProcessingException e) {
            log.error("❌ Failed saving response", e);
        }
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
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion((JsonInclude.Include.NON_NULL));

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);

//        log.info("Outgoing RCS Template Message Request----> \n: {}",json);
        return tokenManager.getValidToken(keyId, key, REQUEST_CHANNEL_KEY)
                .flatMap(accessToken -> sendMessageWithToken(request, accessToken))
                .onErrorResume(ex -> {
                    // If token expired (403), regenerate and retry once
                    if (ex.getMessage() != null && ex.getMessage().contains("403")) {
                        log.warn("!Access token might be expired or not authorized, regenerating and retrying...");
                        return tokenManager.regenerateAccessToken(keyId, key, REQUEST_CHANNEL_KEY)
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
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
                        MediaUploadResponse resp=new MediaUploadResponse(acknowledgementId);
                        MediaResponseEntity entity=new MediaResponseEntity(resp,requestDTO.getMediaUrl());
                        mediaResponseService.saveToDb(entity);
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
