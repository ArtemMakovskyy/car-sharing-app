package com.personal.carsharing.carsharingapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        // Implement health check logic here
        // You can perform database, external service, or any other health checks here

        // If the application is healthy, return a success message
        // TODO: 07.11.2023 ResponseEntity
        // TODO: 07.11.2023 Spring Boot Actuator
        return ResponseEntity.ok("Health check passed. Application is running smoothly.");
    }
}