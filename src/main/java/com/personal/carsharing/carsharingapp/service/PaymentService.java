package com.personal.carsharing.carsharingapp.service;

import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    List<PaymentResponseDto> findAllByRental_User_Id(Long userId, Pageable pageable);

    PaymentResponseDto createPaymentSession(CreatePaymentSessionDto createPaymentSessionDto);

    String handleSuccessfulPayment(String sessionId);

    String processPaymentCancellation(String sessionId);
}
