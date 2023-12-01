package com.personal.carsharing.carsharingapp.exception;

public class StripeProcessException extends RuntimeException {
    public StripeProcessException(String message, Exception e) {
        super(message);
    }
}
