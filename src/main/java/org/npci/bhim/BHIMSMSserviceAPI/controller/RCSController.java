package org.npci.bhim.BHIMSMSserviceAPI.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.modelRCS.RCSTextMessageRequest;
import org.npci.bhim.BHIMSMSserviceAPI.service.AuthenticateService;
import org.npci.bhim.BHIMSMSserviceAPI.service.MessageServiceRCS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conv/RCS")
@Slf4j
public class RCSController {
    @Autowired
    private MessageServiceRCS messageServiceRCS;
    @Autowired
    private AuthenticateService authenticateService;

    @PostMapping("/sendRCSTextMessage")
    public Object sendMessage(@RequestBody RCSTextMessageRequest request) throws JsonProcessingException {

        ObjectMapper mapper=new ObjectMapper();

        mapper.setSerializationInclusion((JsonInclude.Include.NON_NULL));

        String json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);

        log.info("Outgoing RCS Text Message Request----> \n: {}",json);

        return messageServiceRCS.sendMessaage(request);

    }
}
