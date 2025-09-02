package org.npci.bhim.BHIMSMSserviceAPI.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;
import org.npci.bhim.BHIMSMSserviceAPI.model.Consent;
import org.npci.bhim.BHIMSMSserviceAPI.model.GetConsentData;
import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticateService {
    @Autowired
    private WebClient webClient;

    public Mono<ResponseEntity<Map<String, Object>>> sendRegRequest(Registration request) {
        MultiValueMap<String,String> formData=new LinkedMultiValueMap<>();
        formData.add("grant_type",request.getGrant_type());
        formData.add("client_id",request.getClient_id());
        formData.add("username",request.getUsername());
        formData.add("password",request.getPassword());

        final String PROD_URL=String.format("https://auth.aclwhatsapp.com/realms/ipmessaging/protocol/openid-connect/token");
        final String UAT_URL=String.format("https://apiuat.aclwhatsapp.com/auth/realms/ipmessaging/protocol/openid-connect/token");
        return webClient.post()
                .uri(PROD_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.CACHE_CONTROL,"no-cache")
                .bodyValue(formData)
                .exchangeToMono(clientResponse -> {
                    log.info("Http Status code: {}",clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                            .doOnNext(body -> log.info("Response Body :{} ",body))
                            .map(body->ResponseEntity.status(clientResponse.statusCode()).body(body));

                });
    }

//    public Mono<Map<String, Object>> optInRequest(Consent reuest){
////        String token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxNGhacWFGd1hZalRoXzh5Q0lFZGJ4NFo2eXFMOXBnTngtZTZydS1HRmtvIn0.eyJleHAiOjE3NTY3OTI4ODYsImlhdCI6MTc1NjcwNjQ4NiwianRpIjoiMTA2N2Q1MzctMTZhYy00NjIxLTk2MmMtNTM3NzVmMjVjNGViIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLmFjbHdoYXRzYXBwLmNvbS9yZWFsbXMvaXBtZXNzYWdpbmciLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiY2E2YzM5ZWEtNGY0OS00NjEzLTg2ZmItNWI3Nzk3ZjU3ODk0IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiaXBtZXNzYWdpbmctY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6ImYxY2U0ZDQ5LTU0YjAtNDAxZS04ZDg3LTBmZjQyYTBlZTFhZSIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1jb252ZXJzYXRpb25hbC1yZWFsbSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUgc2VuZGVyIGFwcElkIiwic2lkIjoiZjFjZTRkNDktNTRiMC00MDFlLThkODctMGZmNDJhMGVlMWFlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJzZW5kZXIiOiI5MTgyOTExMTkxOTEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJucGNpYmhpbXBkIn0.mL2bYixFY8hRN1Z7rDtWv5m0doYodgoN0PCm_2Yx3v0p3ePMFqzIpldWAyJb6pNkg37L5BCiZ2qJ-inA0HuSAl3G0ltKkuTdPddFKm-0HU1gf2R-PGmGrCNo222mDFNcr0ujaeyW04lseY_5CM_fNQQYRNoHCYN0tSBzgP0VuMbuwN3hL2cHeJN5EJ2RDCFI3dtulGFMKeFHNBElwInSFQIu1L5ugeJTV3JJuiueNR02mM6WPpCLKJdWjnItiIrHZtMwu9eIidUXl_Avg3VDccHykIY79G2FGQeswyduDZIi0p2UWVtZIiPSW2nZueXjZGUPvolJNoPDAp_jQsyAJA";
//        return webClient.post()
//                .uri("https://optin.aclwhatsapp.com/api/v1/wa/optin/bulk")
////                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .bodyValue(reuest)
//                .exchangeToMono(clientResponse -> {
//                    System.out.println("Http Status code: "+clientResponse.statusCode().value());
//                    return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//                            })
//                            .doOnNext(body -> System.out.println("Response Body : "+body));
//
//                });
//    }

public Mono<Map<String, Object>> optInRequest(Consent request) {
    String token= ConvAPIConstants.token;
    final String PROD_URL=String.format("https://optin.aclwhatsapp.com/api/v1/optin/bulk");
    final String UAT_URL=String.format("https://pushuat.aclwhatsapp.com/api/v1/wa/optin/bulk");
    return webClient.post()
            .uri(PROD_URL)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchangeToMono(clientResponse -> {
                HttpStatus status = (HttpStatus) clientResponse.statusCode();
                System.out.println("HTTP Status code: " + status.value());

                MediaType contentType = clientResponse.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

                // If response is JSON, parse it into Map
                if (status.is2xxSuccessful()) {
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

    public Mono<Map<String, Object>> optOutRequest(Consent request) {
        String token= ConvAPIConstants.token;
        final String  PROD_URL=String.format("https://optin.aclwhatsapp.com/api/v1/optout/bulk");
        final String UAT_URL=String.format("https://pushuat.aclwhatsapp.com/api/v1/wa/optout/bulk");
        return webClient.post()
                .uri(PROD_URL)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchangeToMono(clientResponse -> {
                    HttpStatus status = (HttpStatus) clientResponse.statusCode();
                    System.out.println("HTTP Status code: " + status.value());

                    MediaType contentType = clientResponse.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

                    // If response is JSON, parse it into Map
                    if (status.is2xxSuccessful()) {
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

    public Mono<byte[]> getConsent(GetConsentData request) {
        String token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxNGhacWFGd1hZalRoXzh5Q0lFZGJ4NFo2eXFMOXBnTngtZTZydS1HRmtvIn0.eyJleHAiOjE3NTY3OTI4ODYsImlhdCI6MTc1NjcwNjQ4NiwianRpIjoiMTA2N2Q1MzctMTZhYy00NjIxLTk2MmMtNTM3NzVmMjVjNGViIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLmFjbHdoYXRzYXBwLmNvbS9yZWFsbXMvaXBtZXNzYWdpbmciLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiY2E2YzM5ZWEtNGY0OS00NjEzLTg2ZmItNWI3Nzk3ZjU3ODk0IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiaXBtZXNzYWdpbmctY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6ImYxY2U0ZDQ5LTU0YjAtNDAxZS04ZDg3LTBmZjQyYTBlZTFhZSIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1jb252ZXJzYXRpb25hbC1yZWFsbSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUgc2VuZGVyIGFwcElkIiwic2lkIjoiZjFjZTRkNDktNTRiMC00MDFlLThkODctMGZmNDJhMGVlMWFlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJzZW5kZXIiOiI5MTgyOTExMTkxOTEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJucGNpYmhpbXBkIn0.mL2bYixFY8hRN1Z7rDtWv5m0doYodgoN0PCm_2Yx3v0p3ePMFqzIpldWAyJb6pNkg37L5BCiZ2qJ-inA0HuSAl3G0ltKkuTdPddFKm-0HU1gf2R-PGmGrCNo222mDFNcr0ujaeyW04lseY_5CM_fNQQYRNoHCYN0tSBzgP0VuMbuwN3hL2cHeJN5EJ2RDCFI3dtulGFMKeFHNBElwInSFQIu1L5ugeJTV3JJuiueNR02mM6WPpCLKJdWjnItiIrHZtMwu9eIidUXl_Avg3VDccHykIY79G2FGQeswyduDZIi0p2UWVtZIiPSW2nZueXjZGUPvolJNoPDAp_jQsyAJA";
        String url=String.format("https://smartta.aclwhatsapp.com/optindata/v1/"+request.getFromDate().toString()+"/"+request.getToDate().toString());
        System.out.println(url);
//        return webClient.get()
//                .uri(url)
//                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
//                .exchangeToMono(clientResponse -> {
//                    HttpStatus status = (HttpStatus) clientResponse.statusCode();
//                    System.out.println("HTTP Status code: " + status.value());
//
////                    MediaType contentType = clientResponse.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
//
//                    // If response is JSON, parse it into Map
//                        return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
//                                .doOnNext(body -> log.info("Response Body: {}", body))
//                                .map(body ->ResponseEntity.status(clientResponse.statusCode()).body(body));
//                });
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .exchangeToMono(response -> {
                    log.info("Client response code : {}",response.statusCode().value());
                    return response.bodyToMono(byte[].class)
                            .doOnNext(bytes -> {
                                String JsonString=new String(bytes, StandardCharsets.UTF_8);
                                ObjectMapper mapper=new ObjectMapper();
                                try {
                                    List<Map<String,Object>> parsedList=mapper.readValue(JsonString, new TypeReference<List<Map<String, Object>>>(){});
                                    String json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedList);
                                    log.info("List of Consents:\n {}",parsedList);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }

                                log.info("Raw Response: {} ",new String(bytes, StandardCharsets.UTF_8));
                            });
                });
    }
}
