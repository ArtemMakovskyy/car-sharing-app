package com.personal.carsharing.carsharingapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Health check passed. "
                + "Application is running smoothly.");
    }
}
