package com.personal.carsharing.carsharingapp.dto.internal.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalDto {
    private Long id;
    @Schema(example = "2023-12-10")
    private LocalDate rentalDate;
    @Schema(example = "2023-12-16")
    private LocalDate returnDate;
    @Schema(example = "2023-12-20")
    private LocalDate actualReturnDate;
    private Long carId;
    private Long userId;
}
