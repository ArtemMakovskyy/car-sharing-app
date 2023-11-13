package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.user.UpdateUserRoleDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PutMapping("/{id}/role")
    public UserResponseDto updateUserRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRoleDto roleDto,
            Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return userService.updateRole(user.getId(), roleDto.role());
    }

    @GetMapping("/me")
    public UserResponseDto getMyProfileInfo(Authentication authentication) {
        return userService.getUserFromAuthentication(authentication);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMyProfileInfo(
            //            @RequestBody UserProfileUpdateRequest request
    ) {
        // Implement update my profile info logic and return appropriate response
        return null;
    }
}
