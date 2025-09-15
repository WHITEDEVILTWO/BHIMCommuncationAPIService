package org.npci.bhim.BHIMSMSserviceAPI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.npci.bhim.BHIMSMSserviceAPI.repoServices.WADLRReportService;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.WADeliveryReport;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/communcation/callback")
@Slf4j
public class CallBackController {

    private final WADLRReportService wadlrReportService;
    private final ObjectMapper mapper;

    private static final Map<String,WADeliveryReport> callbackList=new TreeMap<>();

    public CallBackController(WADLRReportService wadlrReportService, ObjectMapper mapper) {
        this.wadlrReportService = wadlrReportService;
        this.mapper = mapper;
    }


    @PostMapping("/wa")
    public void receiveCallback(@RequestBody WADeliveryReport payload){

        String key=payload.messageId();
        log.info("Received Sich Call back: {}",key);
        try {
            wadlrReportService.saveReportToDb(payload);
            log.info("Report Saved to db: {}",key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save to DB ",e);
        }
        return;
    }
    @PostMapping("/rcs")
    public void receiveCallback(@RequestBody Map<String,Object> payload){

        String key="";
                log.info("Received Sich Call back: {}",key);
        return;
    }


}
