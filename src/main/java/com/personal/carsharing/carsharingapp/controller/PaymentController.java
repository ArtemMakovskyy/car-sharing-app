package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;
import com.personal.carsharing.carsharingapp.service.PaymentService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {
    private final PaymentService paymentService;

    //    https://github.com/stripe/stripe-java
    //    https://stripe.com/docs/api?lang=java

    @GetMapping("/")
    public List<PaymentResponseDto> getPayments(@RequestParam Long userId) {
        // TODO: 25.11.2023 GET: /payments/?user_id=... - get payments
        return Collections.emptyList();
    }

    @PostMapping("/")
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid CreatePaymentSessionDto createPaymentSessionDto) {
        return paymentService.createPaymentSession(createPaymentSessionDto);
    }

    @GetMapping("/success/")
    public String checkSuccessfulPayments() {
        return "success";
    }

    @GetMapping("/cancel/")
    public String returnPaymentPausedMessage() {
        return "cancel";
    }
}
