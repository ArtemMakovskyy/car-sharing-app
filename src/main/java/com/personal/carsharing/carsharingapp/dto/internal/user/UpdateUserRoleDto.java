package com.personal.carsharing.carsharingapp.dto.internal.user;

import com.personal.carsharing.carsharingapp.validation.ValidUserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRoleDto(
        @ValidUserRole
        @NotBlank(message = "Role should not be blank")
        @Schema(example = "ROLE_CUSTOMER | ROLE_MANAGER | ROLE_ADMIN")
        String role) {
}
