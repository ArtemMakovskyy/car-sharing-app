package com.personal.carsharing.carsharingapp.service.strategy.payment;

import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public interface PriceHandler {
    Integer getTotalPrice(RentalDto rentalDto,
                          BigDecimal carDailyFee,
                          BigDecimal fineMultiplier,
                          BigDecimal smallChangeIsInBanknote);

    default int calculationPriceByPeriodAndIsFine(
            BigDecimal period,
            BigDecimal dailyFee,
            boolean isFine,
            BigDecimal fineMultiplier,
            BigDecimal smallChangeIsInBanknote) {
        BigDecimal price = period.multiply(dailyFee).multiply(smallChangeIsInBanknote);
        if (isFine) {
            price = price.multiply(fineMultiplier);
        }
        System.out.println(price + " " + fineMultiplier);
        return price.intValue();
    }

    default BigDecimal daysInRent(LocalDate start, LocalDate end, boolean isAddDay) {
        if (isAddDay) {
            end = end.plusDays(1);
        }
        return new BigDecimal(ChronoUnit.DAYS.between(start, end));
    }
}
