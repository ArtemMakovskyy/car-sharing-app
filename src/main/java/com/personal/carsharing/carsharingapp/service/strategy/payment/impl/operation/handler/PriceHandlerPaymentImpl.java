package com.personal.carsharing.carsharingapp.service.strategy.payment.impl.operation.handler;

import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceHandler;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class PriceHandlerPaymentImpl implements PriceHandler {

    @Override
    public Integer getTotalPrice(RentalDto rentalDto, BigDecimal carDailyFee,
                                 BigDecimal fineMultiplier, BigDecimal smallChangeIsInBanknote) {
        if (rentalDto.getReturnDate().isEqual(rentalDto.getActualReturnDate())
                || rentalDto.getActualReturnDate().isBefore(rentalDto.getReturnDate())) {
            BigDecimal daysInRent = daysInRent(rentalDto.getRentalDate(),
                    rentalDto.getActualReturnDate(), true);
            return calculationPriceByPeriodAndIsFine(
                    daysInRent, carDailyFee, false, fineMultiplier, smallChangeIsInBanknote);
        }
        BigDecimal daysInRentWithoutFine = daysInRent(rentalDto.getRentalDate(),
                rentalDto.getReturnDate(), true);
        BigDecimal daysInRentWithFine = daysInRent(rentalDto.getReturnDate(),
                rentalDto.getActualReturnDate(), false);
        return calculationPriceByPeriodAndIsFine(daysInRentWithoutFine,
                carDailyFee,
                false,
                fineMultiplier,
                smallChangeIsInBanknote)
                + calculationPriceByPeriodAndIsFine(daysInRentWithFine,
                carDailyFee,
                true,
                fineMultiplier,
                smallChangeIsInBanknote);
    }
}
