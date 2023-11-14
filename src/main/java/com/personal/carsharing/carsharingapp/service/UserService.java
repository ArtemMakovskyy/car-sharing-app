package com.personal.carsharing.carsharingapp.service;

import com.personal.carsharing.carsharingapp.dto.internal.user.UserRegistrationRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.exception.RegistrationException;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;

    UserResponseDto getUserFromAuthentication(Authentication authentication);

    UserResponseDto updateRole(Long userId, String role);

    UserResponseDto updateInfo(
            Authentication authentication, UserRegistrationRequestDto requestDto);
}
