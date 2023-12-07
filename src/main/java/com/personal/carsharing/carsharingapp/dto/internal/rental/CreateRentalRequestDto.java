package com.personal.carsharing.carsharingapp.dto.internal.rental;

import com.personal.carsharing.carsharingapp.validation.ValidDateOrderConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@ValidDateOrderConstraint(
        field = "rentalDate",
        fieldMatch = "returnDate",
        message = "RentalDate should be earlier then returnDate"
)
public class CreateRentalRequestDto {
    @Schema(example = "2023-12-06")
    @NotNull(message = "rentalDate should not be null")
    @Future(message = "rentalDate should be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDate rentalDate;

    @NotNull(message = "returnDate should not be null")
    @Future(message = "returnDate should be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(example = "2023-12-16")
    private LocalDate returnDate;

    @NotNull(message = "Please enter carId ")
    @Schema(example = "2")
    private Long carId;
}
