package com.esalehi.appconfigdemo.controller;

import com.esalehi.appconfigdemo.service.AwsDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/awsdemo")
@Controller
public class DemoController {

    @Autowired
    private AwsDemoService awsDemoService;

    @RequestMapping("/getappconfig")
    public ResponseEntity<String> getConfiguration() {
        return new ResponseEntity<>(awsDemoService.getConfiguration(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "addmetrics")
    public ResponseEntity addMetrics() {
        awsDemoService.putMetricsToCW(1.0);
        return ResponseEntity.ok("metrics put successfully");
    }


}
