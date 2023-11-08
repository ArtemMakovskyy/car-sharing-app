package com.personal.carsharing.carsharingapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("API Documentation Overview: http://localhost:8088/api/swagger-ui/index.html#/");
        logger.info("http://localhost:8080/api/health");
    }

}
