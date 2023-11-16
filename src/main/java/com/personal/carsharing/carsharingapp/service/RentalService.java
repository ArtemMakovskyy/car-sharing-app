package com.personal.carsharing.carsharingapp.service;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalDto add(CreateRentalRequestDto requestDto, Authentication authentication);

    List<RentalDto> findAllByUserIdAndStatus(Long userId, Boolean isActive, Pageable pageable);

    RentalDto getUserRentalDetailsByAuthentication(Authentication authentication);

    RentalDto returnRentalCar(Authentication authentication);
}
