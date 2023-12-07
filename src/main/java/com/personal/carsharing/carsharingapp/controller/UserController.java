package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.user.UpdateUserRoleDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserRegistrationRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management",
        description = "Endpoints for managing users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "Update user role by user id",
            description = "Update user role by user identification number. Only for ADMIN")
    public UserResponseDto updateUserRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRoleDto roleDto) {
        return userService.updateRole(id, roleDto.role());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Get user profile",
            description = "CUSTOMER can get his own profile information")
    public UserResponseDto getMyProfile(Authentication authentication) {
        return userService.getUserFromAuthentication(authentication);
    }

    @Operation(summary = "Update the user profile",
            description = "CUSTOMER can update his profile information")
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    public UserResponseDto updateUserProfile(
            @RequestBody @Valid UserRegistrationRequestDto requestDto,
            Authentication authentication) {
        return userService.updateInfo(authentication, requestDto);
    }
}
