package com.personal.carsharing.carsharingapp.dto.internal.car;

import com.personal.carsharing.carsharingapp.validation.ValidCarType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateCarRequestDto(
        @Schema(example = "Ford")
        @NotBlank(message = "Model should not be blank")
        String model,
        @Schema(example = "Focus")
        @NotBlank(message = "Brand should not be blank")
        String brand,
        @Schema(example = "HATCHBACK")
        @NotBlank(message = "Type should not be blank")
        @ValidCarType
        String type,
        @Min(value = 0, message = "Invalid inventory, it cannot be less than zero")
        @Schema(example = "5")
        Integer inventory,
        @Min(value = 0, message = "Invalid dailyFee, it cannot be less than zero")
        @Schema(example = "25.5")
        BigDecimal dailyFee) {
}

