package com.personal.carsharing.carsharingapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Rental {
    private LocalDateTime rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Long carId;
    private Long userId;
}
