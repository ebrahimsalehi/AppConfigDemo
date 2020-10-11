package com.esalehi.appconfigdemo.controller;

import com.amazonaws.services.appconfig.*;
import com.amazonaws.services.appconfig.model.GetConfigurationRequest;
import com.amazonaws.services.appconfig.model.GetConfigurationResult;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClient;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.esalehi.appconfigdemo.service.AwsDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;

@RequestMapping("/awsdemo")
@Controller
public class DemoController {

    @Autowired
    private AwsDemoService awsDemoService;

    @RequestMapping("/getappconfig")
    public ResponseEntity<String> sayHello() {

        return new ResponseEntity<>(awsDemoService.getConfiguration(), HttpStatus.OK);
    }

}
