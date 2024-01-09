package com.personal.carsharing.carsharingapp.tasks.api.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceLineItemCollection;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.InvoiceLineItemCollectionListParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VideoClassFromStripe {
    @Value("${api.stripe.sk.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init_authentication() {
        Stripe.apiKey = stripeApiKey;
    }

    public void passingReqParInHeader() {
        final CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail("some.email@.email.com")
                .build();

        final RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount("acc_***")
                .build();

        try {
            //customize request header
            final Customer customer = Customer.create(params, requestOptions);
            System.out.println(customer);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    public void nestedServiceMethod() {
        final InvoiceLineItemCollectionListParams params =
                InvoiceLineItemCollectionListParams.builder()
                        .setLimit(5L)
                        .build();

        try {
            final Invoice invoice = Invoice.retrieve("in_***");
            final InvoiceLineItemCollection lines =
                    invoice.getLines()
                            .list(params);
            System.out.println(invoice.getId());
            System.out.println(lines);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer() {
        final CustomerUpdateParams params = CustomerUpdateParams.builder()
                .setEmail("new@email.com")
                .build();
        try {
            Customer customer = Customer.retrieve(
                    "cus_P5wZ5M89vNYrkZ"
            );
            final Customer updatedCustomer = customer.update(params);
            System.out.println(customer);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    public void createPaymentIntentToConfirm_Second() throws StripeException {
        final PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setPaymentMethod("pm_card_visa")
                .build();

        final PaymentIntent intent = PaymentIntent.retrieve("pi_***");
        final PaymentIntent confirmedIntent =
                intent.confirm((Map<String, Object>) params);
        System.out.println(confirmedIntent.getId());
    }

    public void createPaymentIntentToConfirm() throws StripeException {
        final PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(1000L)
                .setCurrency("USD")
                .build();

        final PaymentIntent paymentIntent = PaymentIntent.create(params);
        System.out.println(paymentIntent.getId());
        System.out.println(paymentIntent.getStatus());
    }

    public void createPayment() throws StripeException {

        Map<String, Object> automaticPaymentMethods = new HashMap<>();
        automaticPaymentMethods.put("enabled", true);

        Map<String, Object> params = new HashMap<>();
        params.put("amount", 2000);
        params.put("currency", "usd");
        params.put("automatic_payment_methods", automaticPaymentMethods);

        PaymentIntent paymentIntent = PaymentIntent.create(params);
    }

    public void createCustomerWithBuilder() {
        final CustomerCreateParams params = CustomerCreateParams.builder()
                .addPreferredLocale("en")
                .addPreferredLocale("es")

                .setEmail("fedor@gmail.com")
                .setName("Oleg Ivanov")
                .setDescription("som desc")

                .setPaymentMethod("pm_card_visa")
                .setInvoiceSettings(
                        CustomerCreateParams.InvoiceSettings.builder()
                                .setDefaultPaymentMethod("pm_card_visa")
                                .build()
                )

                .setTaxExempt(CustomerCreateParams.TaxExempt.NONE)
                .build();

        try {
            Customer customer = Customer.create(params);
            System.out.println(customer);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    public void createCustomer() {
        Map<String, Object> params = new HashMap<>();
        params.put("description",
                "My First Test Customer (created for API docs at https://www.stripe.com/docs/api)");
        params.put("name", "Petrov Ivan");
        params.put("phone", "+380501234567");

        try {
            Customer customer = Customer.create(params);
            System.out.println(customer);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    public void getAllCustomers() {
        final CustomerListParams params = CustomerListParams.builder()
                //                .setEmail("new@email.com")
                .setLimit(5L)
                .build();
        try {
            final CustomerCollection customers = Customer.list(params);
            System.out.println(customers);
        } catch (StripeException e) {
            throw new RuntimeException("Can't get customers lists by params " + e);
        }
    }

    public void getIndividualCustomer() {
        try {
            final Customer id_customer = Customer.retrieve("cus_P5wZ5M89vNYrkZ");
            System.out.println(id_customer);
        } catch (StripeException e) {
            throw new RuntimeException("Can't get customer by id " + e);
        }
    }

    public void getIndividualCustomerAndUseSecretKEyIntoCode() {
        try {
            final RequestOptions requestOptions = RequestOptions.builder()
                    .setApiKey("sk_***")
                    .build();
            final Customer id_customer = Customer.retrieve("id_customer", requestOptions);
            System.out.println(id_customer);
        } catch (StripeException e) {
            throw new RuntimeException("Can't get customer by id " + e);
        }
    }

}
