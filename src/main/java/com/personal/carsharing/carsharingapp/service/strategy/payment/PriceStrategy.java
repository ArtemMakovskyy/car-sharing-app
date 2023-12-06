package com.personal.carsharing.carsharingapp.service.strategy.payment;

import com.personal.carsharing.carsharingapp.model.Payment;

public interface PriceStrategy {
    PriceHandler get(Payment.Type paymentType);
}
