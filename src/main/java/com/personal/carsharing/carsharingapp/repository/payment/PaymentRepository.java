package com.personal.carsharing.carsharingapp.repository.payment;

import com.personal.carsharing.carsharingapp.model.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByRentalUserId(Pageable pageable, Long userId);

    Optional<Payment> findBySessionId(String sessionId);
}
