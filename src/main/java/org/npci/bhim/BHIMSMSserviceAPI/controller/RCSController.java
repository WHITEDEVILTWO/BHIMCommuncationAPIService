package org.npci.bhim.BHIMSMSserviceAPI.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.rcsMessageRequests.RCSTemplateMessageRequest;
import org.npci.bhim.BHIMSMSserviceAPI.rcsMessageRequests.RCSTextMessageRequest;
import org.npci.bhim.BHIMSMSserviceAPI.service.AuthenticateService;
import org.npci.bhim.BHIMSMSserviceAPI.service.MessageServiceRCS;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conv/RCS")
@Slf4j
@RequiredArgsConstructor
public class RCSController {

    private final MessageServiceRCS messageServiceRCS;

    private final AuthenticateService authenticateService;

    @PostMapping("/sendRCSTextMessage")
    public Object sendMessage(@RequestBody RCSTextMessageRequest request) throws JsonProcessingException {

        ObjectMapper mapper=new ObjectMapper();

        mapper.setSerializationInclusion((JsonInclude.Include.NON_NULL));

        String json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);

        log.info("Outgoing RCS Text Message Request----> \n: {}",json);

        return messageServiceRCS.sendMessaage(request);

    }
    @PostMapping("/sendRCSTemplateMessage")
    public Object sendMessage(@RequestBody RCSTemplateMessageRequest request) throws JsonProcessingException {

        ObjectMapper mapper=new ObjectMapper();

//        mapper.setSerializationInclusion((JsonInclude.Include.NON_NULL));

        String json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);

        log.info("Outgoing RCS Text Message Request----> \n: {}",json);

        return messageServiceRCS.sendMessaage(request);

    }
}
