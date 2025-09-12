package org.npci.bhim.BHIMSMSserviceAPI.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/communication/callback")
public class CallBackController {

    private static final List<Map<String,Object>> callbackList=new CopyOnWriteArrayList<>();

    @PostMapping("/wa")
    public void receiveCallback(@RequestBody Map<String,Object> payload){
        //our logic goes here
        //database pushing
    }
    @PostMapping("/rcs")
    public void receiveCallBackRCS(@RequestBody Map<String,Object> payload){
        //our logic goes here
        //database pushing
    }
}
