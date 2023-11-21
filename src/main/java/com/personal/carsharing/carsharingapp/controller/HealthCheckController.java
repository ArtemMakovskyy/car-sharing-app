package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.service.NotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthCheckController {
    private final NotificationScheduler notificationScheduler;

    @GetMapping
    public ResponseEntity<String> healthCheck(Authentication authentication) {
        notificationScheduler.dailyMessagesAboutCarRentalReturnStatus();
        return ResponseEntity.ok("Health check passed. Application is running smoothly.");
    }
}
