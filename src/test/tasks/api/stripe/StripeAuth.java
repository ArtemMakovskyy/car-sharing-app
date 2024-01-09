package com.personal.carsharing.carsharingapp.tasks.api.stripe;

import com.personal.carsharing.carsharingapp.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerListParams;

public class StripeAuth {
    private PaymentService key;

    public static void main(String[] args) {
        try {
            Stripe.apiKey = "sk_test_51O9wU7BPFbfBPNcAepBx4bzUCPytFCW8L7n1izlWQuB"
                    + "H94p9IRfsGumwe99do3UJL8okpaXaxiAWrAT1aovcyPyN003ZLWY98D";
            final CustomerListParams params = CustomerListParams.builder().build();
            final CustomerCollection customers = Customer.list(params);
            final Customer oneCustomer = Customer.retrieve("codeOfCustomer");
            System.out.println(customers);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
