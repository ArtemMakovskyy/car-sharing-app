package com.personal.carsharing.carsharingapp.service.strategy.payment.impl.operation.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PriceHandlerPaymentImplTest {
    private static final BigDecimal DAILY_FEE = BigDecimal.TEN;
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.2");
    private static final BigDecimal SMALL_CHANGE = new BigDecimal("100");

    private final PriceHandlerPaymentImpl priceHandlerPayment = new PriceHandlerPaymentImpl();

    @Test
    @DisplayName("Calculates payment without fine for on-time return")
    void getTotalPrice_OnTimeReturn_ReturnsBaseAmount() {
        RentalDto rentalDto = rental(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 3),
                LocalDate.of(2026, 1, 3));

        int actual = priceHandlerPayment.getTotalPrice(
                rentalDto, DAILY_FEE, FINE_MULTIPLIER, SMALL_CHANGE);

        assertEquals(3000, actual);
    }

    @Test
    @DisplayName("Calculates payment with fine for overdue return")
    void getTotalPrice_OverdueReturn_ReturnsBasePlusFineAmount() {
        RentalDto rentalDto = rental(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 3),
                LocalDate.of(2026, 1, 5));

        int actual = priceHandlerPayment.getTotalPrice(
                rentalDto, DAILY_FEE, FINE_MULTIPLIER, SMALL_CHANGE);

        assertEquals(5400, actual);
    }

    private RentalDto rental(
            LocalDate rentalDate,
            LocalDate returnDate,
            LocalDate actualReturnDate) {
        RentalDto rentalDto = new RentalDto();
        rentalDto.setRentalDate(rentalDate);
        rentalDto.setReturnDate(returnDate);
        rentalDto.setActualReturnDate(actualReturnDate);
        return rentalDto;
    }
}
