package com.personal.carsharing.carsharingapp.service;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalDto add(CreateRentalRequestDto requestDto, User user);

    List<RentalDto> findAllByUserIdAndStatus(Long userId, Boolean isActive, Pageable pageable);

    RentalDto getUserRentalDetailsByAuthentication(Long userId);

    RentalDto returnRentalCar(Long userId);
}
