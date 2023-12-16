package com.personal.carsharing.carsharingapp.service;

public interface NotificationService {
    boolean sendNotification(String message, Long recipientId);
}
