package com.personal.carsharing.carsharingapp.dto.internal.user;

import com.personal.carsharing.carsharingapp.validation.ValidUserRole;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRoleDto(
        @ValidUserRole
        @NotBlank(message = "Role should not be blank")
        String role) {
}
