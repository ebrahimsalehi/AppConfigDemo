package com.esalehi.appconfigdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AppconfigdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppconfigdemoApplication.class, args);
    }

}
