package com.personal.carsharing.carsharingapp.service.strategy.payment;

import com.personal.carsharing.carsharingapp.model.PaymentType;

public interface PriceStrategy {
    PriceHandler get(PaymentType paymentType);
}
