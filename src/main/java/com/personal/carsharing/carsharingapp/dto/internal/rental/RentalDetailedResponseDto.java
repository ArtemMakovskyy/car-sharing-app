package com.personal.carsharing.carsharingapp.dto.internal.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalDetailedResponseDto {
    @Schema(example = "2023-12-16")
    private LocalDate returnDate;
    private Long userId;
    private String userFirstName;
}
