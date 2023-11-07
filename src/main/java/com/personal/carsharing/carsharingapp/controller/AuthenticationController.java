package com.personal.carsharing.carsharingapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
    //            @RequestBody UserRegistrationRequest request
    ) {
        // Implement registration logic and return appropriate response
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
    //            @RequestBody UserLoginRequest request
    ) {
        // Implement login logic and return JWT tokens
        return null;
    }
}
