package com.personal.carsharing.carsharingapp.tasks.api.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class StripeSessionExample {

    public static void main(String[] args) {
        // Устанавливаем ключ API Stripe
        Stripe.apiKey = "sk_test_51O9wU7BPFbfBPNcAepBx4bzUCPytFCW8L7n1i"
                + "zlWQuBH94p9IRfsGumwe99do3UJL8okpaXaxiAWrAT1aovcyPyN003ZLWY98D";

        try {
            // Создаем параметры для сессии
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("https://example.com/success")
                            .setCancelUrl("https://example.com/cancel")
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice("your_price_id")
                                            .setQuantity(1L)
                                            .build()
                            )
                            .build();

            // Создаем сессию
            Session session = Session.create(params);

            // Получаем идентификатор сессии
            String sessionId = session.getId();

            // Выводим идентификатор сессии
            System.out.println("Session ID: " + sessionId);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }
}
