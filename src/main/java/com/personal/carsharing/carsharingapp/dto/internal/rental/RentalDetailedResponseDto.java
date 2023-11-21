package com.personal.carsharing.carsharingapp.dto.internal.rental;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalDetailedResponseDto {
    private LocalDate returnDate;
    private Long userId;
    private String userFirstName;
}
