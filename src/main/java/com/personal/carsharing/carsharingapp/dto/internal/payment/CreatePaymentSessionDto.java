package com.personal.carsharing.carsharingapp.dto.internal.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentSessionDto(
        @NotNull @Positive Long rentalId,
        @Schema(example = "PAYMENT | FINE")
        @NotBlank String paymentType) {
}
