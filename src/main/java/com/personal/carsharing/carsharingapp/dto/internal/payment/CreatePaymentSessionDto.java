package com.personal.carsharing.carsharingapp.dto.internal.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentSessionDto(
        @NotNull @Positive Long rentalId,
        @NotBlank String paymentType) {
}
