package com.personal.carsharing.carsharingapp.service;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalDto add (CreateRentalRequestDto requestDto, Authentication authentication);
}
