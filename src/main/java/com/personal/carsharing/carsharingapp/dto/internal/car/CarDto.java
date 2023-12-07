package com.personal.carsharing.carsharingapp.dto.internal.car;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record CarDto(
        @Schema(example = "1")
        Long id,
        @Schema(example = "Ford")
        String model,
        @Schema(example = "Focus")
        String brand,
        @Schema(example = "HATCHBACK")
        String type,
        @Schema(example = "5")
        Integer inventory,
        @Schema(example = "25.5")
        BigDecimal dailyFee) {
}
