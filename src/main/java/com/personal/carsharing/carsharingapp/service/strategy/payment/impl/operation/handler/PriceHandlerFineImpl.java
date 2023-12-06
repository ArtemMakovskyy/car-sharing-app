package com.personal.carsharing.carsharingapp.service.strategy.payment.impl.operation.handler;

import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceHandler;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class PriceHandlerFineImpl implements PriceHandler {
    @Override
    public Integer getTotalPrice(RentalDto rentalDto,
                                 BigDecimal carDailyFee,
                                 BigDecimal fineMultiplier,
                                 BigDecimal smallChangeIsInBanknote) {
        BigDecimal daysInRent = daysInRent(
                rentalDto.getRentalDate(), rentalDto.getActualReturnDate(), true);
        return calculationPriceByPeriodAndIsFine(
                daysInRent,
                carDailyFee,
                true,
                fineMultiplier,
                smallChangeIsInBanknote);
    }
}
