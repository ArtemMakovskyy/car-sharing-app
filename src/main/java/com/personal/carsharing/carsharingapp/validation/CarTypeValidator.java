package com.personal.carsharing.carsharingapp.validation;

import com.personal.carsharing.carsharingapp.model.Car;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class CarTypeValidator implements ConstraintValidator<ValidCarType, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        Car.Type[] validTypes = Car.Type.values();
        return Arrays.stream(validTypes)
                .map(Enum::name)
                .anyMatch(validType -> validType.equals(value.toUpperCase()));
    }
}
