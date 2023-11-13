package com.personal.carsharing.carsharingapp.dto.internal.car;

import java.math.BigDecimal;

public record CarDto(

        Long id,

        String model,

        String brand,

        String type,

        Integer inventory,

        BigDecimal dailyFee) {
}
