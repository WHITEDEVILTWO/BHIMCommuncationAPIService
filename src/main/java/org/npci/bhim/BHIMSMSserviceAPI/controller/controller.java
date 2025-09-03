package org.npci.bhim.BHIMSMSserviceAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.model.Consent;
import org.npci.bhim.BHIMSMSserviceAPI.model.GetConsentData;
import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.npci.bhim.BHIMSMSserviceAPI.model.TextMsgRequest;
import org.npci.bhim.BHIMSMSserviceAPI.service.AuthenticateService;
import org.npci.bhim.BHIMSMSserviceAPI.service.MessageService;
import org.npci.bhim.BHIMSMSserviceAPI.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/whatsapp")
@Slf4j
public class controller {

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void logObjectMapperModules() {
        objectMapper.getRegisteredModuleIds().forEach(id -> System.out.println("Registered module: " + id));
    }

    @Autowired
    private AuthenticateService authenticateService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisService redisService;

    @PostMapping("/getToken")
    public Mono<ResponseEntity<Map<String,Object>>> getToken(@ModelAttribute Registration request) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
        Object accessToken = redisService.get("access_token").block();
        log.info("Access Token from controller : {} ",accessToken);
        return authenticateService.sendRegRequest(request);
    }
    @PostMapping("/sendmessage")
    public Mono<Map<String, Object>> sedMessage(@RequestBody TextMsgRequest request) throws JsonProcessingException {

        ObjectMapper mapper=new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));

        return messageService.sendMessaage(request);
    }

    @PostMapping("/optin")
    public Mono<Map<String, Object>> sedMessageOptIn(@RequestBody Consent request) throws JsonProcessingException {

//        ObjectMapper mapper=new ObjectMapper();
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
//
        return authenticateService.optInRequest(request);
    }

    @PostMapping("/optout")
    public Mono<Map<String, Object>> sedMessage(@RequestBody Consent optoutRequest) throws JsonProcessingException {

//        ObjectMapper mapper=new ObjectMapper();
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));

        return authenticateService.optOutRequest(optoutRequest);
    }



    @GetMapping("/getoptin")
    public Mono<byte[]> sedMessage(@RequestBody GetConsentData request) throws JsonProcessingException {

        ObjectMapper mapper=new ObjectMapper();
        log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
//        LocalDate from = LocalDate.parse(request.getFromDate());
//        LocalDate to = LocalDate.parse(request.getToDate());

        return authenticateService.getConsent(request);
    }


}
