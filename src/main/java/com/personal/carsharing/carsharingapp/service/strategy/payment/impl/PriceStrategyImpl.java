package com.personal.carsharing.carsharingapp.service.strategy.payment.impl;

import com.personal.carsharing.carsharingapp.model.Payment;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceHandler;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceStrategy;
import com.personal.carsharing.carsharingapp.service.strategy.payment.impl.operation.handler.PriceHandlerFineImpl;
import com.personal.carsharing.carsharingapp.service.strategy.payment.impl.operation.handler.PriceHandlerPaymentImpl;
import java.util.HashMap;
import org.springframework.stereotype.Component;

@Component
public class PriceStrategyImpl implements PriceStrategy {
    private final HashMap<Payment.Type, PriceHandler> paymentStrategy;

    public PriceStrategyImpl() {
        paymentStrategy = new HashMap<>();
        paymentStrategy.put(Payment.Type.PAYMENT, new PriceHandlerPaymentImpl());
        paymentStrategy.put(Payment.Type.FINE, new PriceHandlerFineImpl());
    }

    @Override
    public PriceHandler get(Payment.Type paymentType) {
        return paymentStrategy.get(paymentType);
    }
}
