package com.personal.carsharing.carsharingapp.validation;

import com.personal.carsharing.carsharingapp.model.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class UserRoleValidator implements ConstraintValidator<ValidUserRole, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        final Role.RoleName[] validRoles = Role.RoleName.values();

        return Arrays.stream(validRoles)
                .map(Enum::name)
                .anyMatch(validType -> validType.equals(value.toUpperCase()));
    }
}
