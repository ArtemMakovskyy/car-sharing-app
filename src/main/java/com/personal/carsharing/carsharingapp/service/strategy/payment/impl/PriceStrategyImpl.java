package com.personal.carsharing.carsharingapp.service.strategy.payment.impl;

import com.personal.carsharing.carsharingapp.model.PaymentType;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceHandler;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceStrategy;
import com.personal.carsharing.carsharingapp.service.strategy.payment.impl.operation.handler.PriceHandlerFineImpl;
import com.personal.carsharing.carsharingapp.service.strategy.payment.impl.operation.handler.PriceHandlerPaymentImpl;
import java.util.HashMap;
import org.springframework.stereotype.Component;

@Component
public class PriceStrategyImpl implements PriceStrategy {
    private final HashMap<PaymentType, PriceHandler> paymentStrategy;

    public PriceStrategyImpl() {
        paymentStrategy = new HashMap<>();
        paymentStrategy.put(PaymentType.PAYMENT, new PriceHandlerPaymentImpl());
        paymentStrategy.put(PaymentType.FINE, new PriceHandlerFineImpl());
    }

    @Override
    public PriceHandler get(PaymentType paymentType) {
        return paymentStrategy.get(paymentType);
    }
}
