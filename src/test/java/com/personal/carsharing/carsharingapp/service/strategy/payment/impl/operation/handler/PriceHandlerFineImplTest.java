package com.personal.carsharing.carsharingapp.service.strategy.payment.impl.operation.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PriceHandlerFineImplTest {
    private static final BigDecimal DAILY_FEE = BigDecimal.TEN;
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.2");
    private static final BigDecimal SMALL_CHANGE = new BigDecimal("100");

    private final PriceHandlerFineImpl priceHandlerFine = new PriceHandlerFineImpl();

    @Test
    @DisplayName("Calculates full-period fine amount")
    void getTotalPrice_ValidDates_ReturnsFineAmount() {
        RentalDto rentalDto = new RentalDto();
        rentalDto.setRentalDate(LocalDate.of(2026, 1, 1));
        rentalDto.setActualReturnDate(LocalDate.of(2026, 1, 3));

        int actual = priceHandlerFine.getTotalPrice(
                rentalDto, DAILY_FEE, FINE_MULTIPLIER, SMALL_CHANGE);

        assertEquals(3600, actual);
    }
}
