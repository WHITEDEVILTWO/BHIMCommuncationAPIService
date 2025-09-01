package org.npci.bhim.BHIMSMSserviceAPI.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/waAPICallBack/sinch")
@Slf4j
public class CallBackController {

    private static final List<Map<String,Object>> callbackList=new CopyOnWriteArrayList<>();
    @PostMapping
    public ResponseEntity<Void> receiveCallback(@RequestBody Map<String,Object> payload){
        callbackList.add(payload);
        log.info("Received Sich Call back: {}",payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String,Object>>> getCallBacks(){
        return ResponseEntity.ok(callbackList);
    }
}
