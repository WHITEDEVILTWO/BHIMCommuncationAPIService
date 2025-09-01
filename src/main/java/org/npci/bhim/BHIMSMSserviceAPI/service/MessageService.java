package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.RequiredArgsConstructor;
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

    public Mono<Map<String, Object>> sendRegRequest(TextMsgRequest request) {
        String token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxNGhacWFGd1hZalRoXzh5Q0lFZGJ4NFo2eXFMOXBnTngtZTZydS1HRmtvIn0.eyJleHAiOjE3NTY3OTI4ODYsImlhdCI6MTc1NjcwNjQ4NiwianRpIjoiMTA2N2Q1MzctMTZhYy00NjIxLTk2MmMtNTM3NzVmMjVjNGViIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLmFjbHdoYXRzYXBwLmNvbS9yZWFsbXMvaXBtZXNzYWdpbmciLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiY2E2YzM5ZWEtNGY0OS00NjEzLTg2ZmItNWI3Nzk3ZjU3ODk0IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiaXBtZXNzYWdpbmctY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6ImYxY2U0ZDQ5LTU0YjAtNDAxZS04ZDg3LTBmZjQyYTBlZTFhZSIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1jb252ZXJzYXRpb25hbC1yZWFsbSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUgc2VuZGVyIGFwcElkIiwic2lkIjoiZjFjZTRkNDktNTRiMC00MDFlLThkODctMGZmNDJhMGVlMWFlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJzZW5kZXIiOiI5MTgyOTExMTkxOTEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJucGNpYmhpbXBkIn0.mL2bYixFY8hRN1Z7rDtWv5m0doYodgoN0PCm_2Yx3v0p3ePMFqzIpldWAyJb6pNkg37L5BCiZ2qJ-inA0HuSAl3G0ltKkuTdPddFKm-0HU1gf2R-PGmGrCNo222mDFNcr0ujaeyW04lseY_5CM_fNQQYRNoHCYN0tSBzgP0VuMbuwN3hL2cHeJN5EJ2RDCFI3dtulGFMKeFHNBElwInSFQIu1L5ugeJTV3JJuiueNR02mM6WPpCLKJdWjnItiIrHZtMwu9eIidUXl_Avg3VDccHykIY79G2FGQeswyduDZIi0p2UWVtZIiPSW2nZueXjZGUPvolJNoPDAp_jQsyAJA";

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
