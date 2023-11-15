package com.personal.carsharing.carsharingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class DateOrderConstraintValidator
        implements ConstraintValidator<ValidDateOrderConstraint, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(ValidDateOrderConstraint constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object fieldValue = new BeanWrapperImpl(value)
                .getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(value)
                .getPropertyValue(fieldMatch);

        if (fieldValue == null || fieldMatchValue == null) {
            return false;
        }

        if (fieldValue instanceof LocalDateTime && fieldMatchValue instanceof LocalDateTime) {
            return ((LocalDateTime) fieldValue).isBefore((LocalDateTime) fieldMatchValue);
        }

        return false;
    }
}