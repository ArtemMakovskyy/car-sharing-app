package com.personal.carsharing.carsharingapp.dto.internal.rental;

import com.personal.carsharing.carsharingapp.validation.ValidDateOrderConstraint;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@ValidDateOrderConstraint(
        field = "rentalDate",
        fieldMatch = "returnDate",
        message = "RentalDate should be earlier then returnDate"
)
public class CreateRentalRequestDto {
    @NotNull(message = "rentalDate should not be null")
    @Future(message = "rentalDate should be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime rentalDate;

    @NotNull(message = "returnDate should not be null")
    @Future(message = "returnDate should be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime returnDate;

    @NotNull(message = "Please enter carId ")
    private Long carId;
}
