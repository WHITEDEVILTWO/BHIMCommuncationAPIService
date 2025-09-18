package org.npci.bhim.BHIMSMSserviceAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.piiDataManagement.DataEncLayer;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.WAResponses;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.MediaUploadRequest;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.WaTextMsgRequest;
import org.npci.bhim.BHIMSMSserviceAPI.model.MediaUpload;
import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.npci.bhim.BHIMSMSserviceAPI.repos.WAResponseRepository;
import org.npci.bhim.BHIMSMSserviceAPI.service.AuthenticateService;
import org.npci.bhim.BHIMSMSserviceAPI.service.MessageService;
import org.npci.bhim.BHIMSMSserviceAPI.service.RedisService;
import org.npci.bhim.BHIMSMSserviceAPI.utils.MediaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/whatsapp")
@Slf4j
public class controller {

    private final AuthenticateService authenticateService;
    private final MessageService messageService;
    private final RedisService redisService;
    private final WAResponseRepository waResponseRepository;
    private final DataEncLayer dataEncLayer;
    @Autowired
    private ObjectMapper objectMapper;

    public controller(AuthenticateService authenticateService, MessageService messageService, RedisService redisService, WAResponseRepository waResponseRepository, DataEncLayer dataEncLayer) {
        this.authenticateService = authenticateService;
        this.messageService = messageService;
        this.redisService = redisService;
        this.waResponseRepository = waResponseRepository;
        this.dataEncLayer = dataEncLayer;
    }

    @PostConstruct
    public void logObjectMapperModules() {
        objectMapper.getRegisteredModuleIds().forEach(id -> System.out.println("Registered module: " + id));
    }

    //@Deprecated
//    @PostMapping("/getToken")
//    public Mono<ResponseEntity<Map<String,Object>>> getToken(@ModelAttribute Registration request) throws JsonProcessingException {
//        ObjectMapper mapper=new ObjectMapper();
////        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
////        Object accessToken = redisService.get("WA_access_token").block();
////        log.info("Access Token from controller : {} ",accessToken);
//        return authenticateService.sendRegRequest(request);
//    }
    @PostMapping("/sendmessage")
    public Mono<Map<String, Object>> sedMessage(@RequestBody WaTextMsgRequest request) throws Exception {

        return dataEncLayer.routeToService(request);
    }
/**
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
*/
    @PostMapping("/uploadMedia")
    public Mono<Map<String, Object>> uploadMedia(@RequestBody MediaUpload request) throws JsonProcessingException {

        ObjectMapper mapper=new ObjectMapper();
        log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
//        LocalDate from = LocalDate.parse(request.getFromDate());
//        LocalDate to = LocalDate.parse(request.getToDate());
        String mediaFormat = MediaUtils.getMediaFormatFromUrl(request.getMediaUrl());

        MediaUploadRequest requestDTO = new MediaUploadRequest();
        requestDTO.setMediaUrl(request.getMediaUrl());
        requestDTO.setMediaFormat(mediaFormat);

        return messageService.sendMediaRequest(requestDTO);
    }


}
