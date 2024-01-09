package com.personal.carsharing.carsharingapp.tasks.api.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class StripePayment {
    public void stripePayment() throws StripeException {
        Stripe.apiKey = "YOUR_SECRET_KEY";

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", 1000);
        chargeParams.put("currency", "usd");
        chargeParams.put("description", "Test Payment");
        chargeParams.put("source", "tok_visa");

        Charge charge = Charge.create(chargeParams);

        System.out.println(charge);
    }

    public void stripeSession() throws StripeException {
        Stripe.apiKey = "YOUR_SECRET_KEY";

        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price", "price_12345");
        lineItem.put("quantity", 1);

        List<Map<String, Object>> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        //        SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
        //                             .setPaymentMethodTypes(paymentMethodTypes)
        //                .setLineItems(lineItems)
        //                .setMode(SessionCreateParams.Mode.PAYMENT)
        //                .setSuccessUrl("https://example.com/success")
        //                .setCancelUrl("https://example.com/cancel");

        //        Session session = Session.create(builder.build());

        //        System.out.println(session);
    }
}
