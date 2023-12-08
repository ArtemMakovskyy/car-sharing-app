package com.personal.carsharing.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @OneToOne(fetch = FetchType.LAZY)
    private Rental rental;

    @Column(nullable = false)
    private URL sessionUrl;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private BigDecimal amountToPay;
}
