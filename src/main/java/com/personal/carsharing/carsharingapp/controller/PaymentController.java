package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoint for managing payments")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/")
    @Operation(summary = """
            Get all user's payments. If user CUSTOMER he can get only own payment, 
            if ADMIN or MANAGER can get any payments.""")
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
    @Operation(summary = "Create stripe payment session")
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid CreatePaymentSessionDto createPaymentSessionDto) {
        return paymentService.createPaymentSession(createPaymentSessionDto);
    }

    @Operation(summary = "Redirect the endpoint in case of successful payment to the stripe")
    @GetMapping("/success")
    public String handleSuccessfulPayment(
            @RequestParam(name = "session_id") String sessionId) {
        return paymentService.handleSuccessfulPayment(sessionId);
    }

    @Operation(summary =
            "Redirect the endpoint in case of payment cancellation or pause to the stripe")
    @GetMapping("/cancel")
    public String processPaymentCancellation(
            @RequestParam(name = "session_id") String sessionId) {
        return paymentService.processPaymentCancellation(sessionId);
    }
}
