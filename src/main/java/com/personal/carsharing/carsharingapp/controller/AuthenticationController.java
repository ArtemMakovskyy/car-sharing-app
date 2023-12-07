package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.user.UserLoginRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserLoginResponseDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserRegistrationRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.exception.RegistrationException;
import com.personal.carsharing.carsharingapp.security.AuthenticationService;
import com.personal.carsharing.carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Management authentication", description = "Endpoints to login and register")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Registration of a new user.",
            description = "Save your: email, password,first name, "
                    + "last name")
    @PostMapping("/register")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "Registered user login.",
            description = "Input email address and password to login.")
    @PostMapping("/login")
    public UserLoginResponseDto loginUser(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
