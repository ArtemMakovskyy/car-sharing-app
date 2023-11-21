package com.personal.carsharing.carsharingapp.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {
    private Long id;

    private PaymentStatus status;

    private PaymentType type;

    private Long rentalId;

    private String sessionUrl;

    private String sessionId;

    private BigDecimal amountToPay;

}
