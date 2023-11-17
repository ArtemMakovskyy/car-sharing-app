package com.personal.carsharing.carsharingapp.service;

public interface NotificationService {
    void sendNotification(String message, Long recipientId);
}
