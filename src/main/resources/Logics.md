@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final WebClient webClient;
    private final RedisService redisService;
    private final TokenManager tokenManager;
    private final WAResponseService waResponseService;

    public static final String REQUEST_CHANNEL_KEY = "WA_access_token";

    @Value("${npci.wa.uname}")
    String keyId;

    @Value("${npci.wa.key}")
    String key;

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
        return tokenManager.regenerateAccessToken(keyId, key, REQUEST_CHANNEL_KEY)
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
}
