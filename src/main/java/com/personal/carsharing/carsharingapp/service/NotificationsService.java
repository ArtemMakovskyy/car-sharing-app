package com.personal.carsharing.carsharingapp.service;

import com.personal.carsharing.carsharingapp.model.Payment;
import com.personal.carsharing.carsharingapp.model.Rental;
import org.springframework.stereotype.Service;

@Service
public class NotificationsService {
    public void sendNewRentalNotification(Rental rental) {
        // Implement logic to send notification about new rental to administrators
    }

    public void sendOverdueRentalNotification(Rental rental) {
        // Implement logic to send notification about overdue rental to administrators
    }

    public void sendSuccessfulPaymentNotification(Payment payment) {
        // Implement logic to send notification about successful payment to administrators
    }
}
