package com.personal.carsharing.carsharingapp.controller;

//import com.personal.carsharing.carsharingapp.service.stripe.StripeService;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Charge;
//import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/payments")
//@AllArgsConstructor
public class PaymentController {
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

    @GetMapping
    public ResponseEntity<?> getPayments(@RequestParam Long userId) {
        // Implement get payments logic and return payment information
        return null;
    }

    @PostMapping
    public ResponseEntity<?> createPaymentSession(
    //            @RequestBody PaymentSessionRequest request
    ) {
        // Implement create payment session logic and return session details
        return null;
    }

    @GetMapping("/success")
    public ResponseEntity<?> handleSuccessfulPayment() {
        // Implement logic for successful payment redirection and return appropriate response
        return null;
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> handleCancelledPayment() {
        // Implement logic for cancelled payment redirection and return appropriate response
        return null;
    }
}
