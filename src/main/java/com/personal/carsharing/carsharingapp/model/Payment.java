package com.personal.carsharing.carsharingapp.model;

import java.math.BigDecimal;

public class Payment {
    private PaymentStatus status;
    private PaymentType type;
    private Long rentalId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}

