package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.user.UpdateUserRoleDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserRegistrationRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.dto.mapper.UserMapper;
import com.personal.carsharing.carsharingapp.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final Validator validator;
    private final UserMapper userMapper;

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public UserResponseDto updateUserRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRoleDto roleDto) {
        return userService.updateRole(id, roleDto.role());
    }

    @GetMapping("/me")
    public UserResponseDto getMyProfileInfo(Authentication authentication) {
        return userService.getUserFromAuthentication(authentication);
    }

    @PutMapping("/me")
    public UserResponseDto totallyUpdateMyProfileInfo(
            @RequestBody @Valid UserRegistrationRequestDto requestDto,
            Authentication authentication) {
        return userService.updateInfo(authentication, requestDto);
    }
}
