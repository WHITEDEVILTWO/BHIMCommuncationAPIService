package org.npci.bhim.BHIMSMSserviceAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.entities.WAResponses;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.MediaUploadRequest;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.WaTextMsgRequest;
import org.npci.bhim.BHIMSMSserviceAPI.model.Consent;
import org.npci.bhim.BHIMSMSserviceAPI.model.GetConsentData;
import org.npci.bhim.BHIMSMSserviceAPI.model.MediaUpload;
import org.npci.bhim.BHIMSMSserviceAPI.model.Registration;
import org.npci.bhim.BHIMSMSserviceAPI.repos.WAResponseRepository;
import org.npci.bhim.BHIMSMSserviceAPI.service.AuthenticateService;
import org.npci.bhim.BHIMSMSserviceAPI.service.MessageService;
import org.npci.bhim.BHIMSMSserviceAPI.service.RedisService;
import org.npci.bhim.BHIMSMSserviceAPI.utils.MediaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/whatsapp")
@Slf4j
public class controller {

    @Autowired
    private ObjectMapper objectMapper;

    public controller(AuthenticateService authenticateService, MessageService messageService, RedisService redisService, WAResponseRepository waResponseRepository) {
        this.authenticateService = authenticateService;
        this.messageService = messageService;
        this.redisService = redisService;
        this.waResponseRepository = waResponseRepository;
    }

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
    @Autowired
    private WAResponseRepository waResponseRepository;

    @PostMapping("/redissavetest")
    public void savetoredistest(){
        redisService.save("Ganesh","BABU");
        WAResponses responses=new WAResponses();
        responses.setResponseId("123456789");
        responses.setRequestBody("{\n" +
                "  \"recipient_type\" : \"individual\",\n" +
                "  \"to\" : \"917893411160\",\n" +
                "  \"type\" : \"template\",\n" +
                "  \"template\" : {\n" +
                "    \"name\" : \"p2p_cohort_19082025\",\n" +
                "    \"language\" : {\n" +
                "      \"policy\" : \"deterministic\",\n" +
                "      \"code\" : \"en\"\n" +
                "    },\n" +
                "    \"components\" : null\n" +
                "  },\n" +
                "  \"metadata\" : {\n" +
                "    \"messageId\" : \"8d3fe6f3-21fc-4130-9c29-5a7be3799340\",\n" +
                "    \"transactionId\" : \"a5da78ac-01b5-4fec-b3eb-0e1402f8f577\",\n" +
                "    \"callbackDlrUrl\" : \"https://bhimupi.com\"\n" +
                "  }\n" +
                "}");
        waResponseRepository.save(responses);
        log.info("Saved to db");

    }
    @PostMapping("/getToken")
    public Mono<ResponseEntity<Map<String,Object>>> getToken(@ModelAttribute Registration request) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
//        Object accessToken = redisService.get("WA_access_token").block();
//        log.info("Access Token from controller : {} ",accessToken);
        return authenticateService.sendRegRequest(request);
    }
    @PostMapping("/sendmessage")
    public Mono<Map<String, Object>> sedMessage(@RequestBody WaTextMsgRequest request) throws JsonProcessingException {

        ObjectMapper mapper=new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));

        return messageService.sendMessage(request);
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
