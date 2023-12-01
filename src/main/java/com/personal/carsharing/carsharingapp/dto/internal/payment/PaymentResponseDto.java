package com.personal.carsharing.carsharingapp.dto.internal.payment;

public record PaymentResponseDto(
        String id,
        String status,
        String type,
        Long rentalId,
        String sessionId,
        Integer amountToPay,
        String sessionUrl) {
}
