package com.personal.carsharing.carsharingapp.service;

import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(CreatePaymentSessionDto createPaymentSessionDto);
}
