package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RetryPolicyService {

    /**
     * Apply retry logic with custom timeouts based on error type.
     */
    public <T> Mono<T> withRetryAndTimeout(Mono<T> mono, String context) {
        return mono
                .timeout(getTimeoutForContext(context)) // pick timeout based on context
                .retryWhen(getRetrySpec(context))       // retry spec based on error type
                .doOnError(ex -> log.error("❌ [{}] Failed after retries: {}", context, ex.getMessage()));
    }

    /**
     * Different timeouts for different contexts
     */
    private Duration getTimeoutForContext(String context) {
        return switch (context) {
            case "connection" -> Duration.ofSeconds(10); // max connection timeout
            case "read" -> Duration.ofSeconds(3);        // read timeout
            case "request" -> Duration.ofSeconds(5);     // request timeout
            case "response" -> Duration.ofSeconds(2);    // expected response timeout
            case "auth" -> Duration.ofSeconds(5);        // auth failure
            default -> Duration.ofSeconds(5);            // fallback
        };
    }

    /**
     * Retry rules depending on error type.
     */
    private Retry getRetrySpec(String context) {
        return Retry.fixedDelay(2, Duration.ofMillis(200))
                .filter(ex ->
                        ex instanceof TimeoutException ||
                        (ex.getMessage() != null && ex.getMessage().contains("500")) ||
                        (ex.getMessage() != null && ex.getMessage().contains("DNSNameResolver")) ||
                        (context.equals("auth") && ex.getMessage() != null && ex.getMessage().contains("401"))
                )
                .doBeforeRetry(sig -> log.warn("🔁 [{}] Retrying due to {}... attempt {}", 
                                               context, sig.failure().getClass().getSimpleName(), sig.totalRetries() + 1));
    }
}
