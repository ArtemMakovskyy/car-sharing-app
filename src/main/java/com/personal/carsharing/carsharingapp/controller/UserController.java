package com.personal.carsharing.carsharingapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id
    //            , @RequestBody UserRoleUpdateRequest request
    ) {
        // Implement update user role logic and return appropriate response
        return null;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfileInfo() {
        // Implement get my profile info logic and return user details
        return null;
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMyProfileInfo(
    //            @RequestBody UserProfileUpdateRequest request
    ) {
        // Implement update my profile info logic and return appropriate response
        return null;
    }
}
