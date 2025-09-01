package org.npci.bhim.BHIMSMSserviceAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.npci.bhim.BHIMSMSserviceAPI.model.Consent;
import org.npci.bhim.BHIMSMSserviceAPI.model.GetConsentData;
import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.npci.bhim.BHIMSMSserviceAPI.model.TextMsgRequest;
import org.npci.bhim.BHIMSMSserviceAPI.service.AuthenticateService;
import org.npci.bhim.BHIMSMSserviceAPI.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/whatsapp")
public class controller {

    @Autowired
    private AuthenticateService authenticateService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/getToken")
    public Mono<ResponseEntity<Map<String,Object>>> getToken(@ModelAttribute Registration request) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
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

//        ObjectMapper mapper=new ObjectMapper();
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
        return authenticateService.getConsent(request);
    }


}
