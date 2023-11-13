package com.personal.carsharing.carsharingapp.dto.internal.car;

import com.personal.carsharing.carsharingapp.validation.ValidCarType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateCarRequestDto(
        @NotBlank(message = "Model should not be blank")
        String model,
        @NotBlank(message = "Brand should not be blank")
        String brand,
        @NotBlank(message = "Type should not be blank")
        @ValidCarType
        String type,
        @Min(value = 0, message = "Invalid inventory, it cannot be less than zero")
        Integer inventory,
        @Min(value = 0, message = "Invalid dailyFee, it cannot be less than zero")
        BigDecimal dailyFee) {
}

