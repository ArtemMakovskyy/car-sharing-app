package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @GetMapping("/")
    public List<PaymentResponseDto> getPaymentsById(
            Pageable pageable,
            @RequestParam(name = "user_id") Long userId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            userId = user.getId();
        }
        return paymentService.findAllByRental_User_Id(userId, pageable);
    }

    @PostMapping("/")
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid CreatePaymentSessionDto createPaymentSessionDto) {
        return paymentService.createPaymentSession(createPaymentSessionDto);
    }

    @GetMapping("/success")
    public String handleSuccessfulPayment(
            @RequestParam(name = "session_id") String sessionId) {
        return paymentService.handleSuccessfulPayment(sessionId);
    }

    @GetMapping("/cancel")
    public String processPaymentCancellation(
            @RequestParam(name = "session_id") String sessionId) {
        return paymentService.processPaymentCancellation(sessionId);
    }
}
