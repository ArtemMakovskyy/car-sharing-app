package com.personal.carsharing.carsharingapp.dto.internal.payment;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentResponseDto(
        String id,
        @Schema(example = "PAID | PENDING")
        String status,
        @Schema(example = "PAYMENT | FINE")
        String type,
        Long rentalId,
        String sessionId,
        Integer amountToPay,
        String sessionUrl) {
}
