package com.personal.carsharing.carsharingapp.controller;

//import com.personal.carsharing.carsharingapp.service.stripe.StripeService;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Charge;
//import lombok.AllArgsConstructor;

import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentDto;
import com.personal.carsharing.carsharingapp.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    // Need to find out
    //    private final StripeService stripeService;

    //    @PostMapping("/charge")
    //    public String chargeCard(
    //            @RequestParam("token") String token,
    //            @RequestParam("amount") double amount) {
    //        try {
    //            Charge charge = stripeService.chargeCreditCard(token, amount);
    //            // Обработка успешного платежа
    //            return "Платеж успешно проведен!";
    //        } catch (StripeException e) {
    //            // Обработка ошибок при платеже
    //            return "Ошибка при проведении платежа: " + e.getMessage();
    //        }
    //    }
    //

    @GetMapping("/")
    public List<PaymentDto> getPayments(@RequestParam Long userId) {
        return List.of(new PaymentDto());
    }

    @PostMapping("/")
    public PaymentDto createPaymentSession(
            @RequestBody CreatePaymentSessionDto request) {
        return new PaymentDto();
    }

    @GetMapping("/success/")
    public boolean checkSuccessfulPayments() {
        // Implement logic for successful payment redirection and return appropriate response
        return true;
    }

    @GetMapping("/cancel/")
    public boolean returnPaymentPausedMessage() {
        // Implement logic for cancelled payment redirection and return appropriate response
        return true;
    }
}
