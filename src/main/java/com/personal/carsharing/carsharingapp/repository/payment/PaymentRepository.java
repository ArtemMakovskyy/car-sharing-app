package com.personal.carsharing.carsharingapp.repository.payment;

import com.personal.carsharing.carsharingapp.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
