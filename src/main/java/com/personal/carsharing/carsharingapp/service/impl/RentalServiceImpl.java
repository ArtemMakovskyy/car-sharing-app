package com.personal.carsharing.carsharingapp.service.impl;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.model.Car;
import com.personal.carsharing.carsharingapp.model.Rental;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.repository.car.CarRepository;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.repository.user.UserRepository;
import com.personal.carsharing.carsharingapp.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Override
    public RentalDto add(
            CreateRentalRequestDto requestDto,
            Authentication authentication) {
        final Car rentedСar = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id " + requestDto.getCarId()));
        carInventoryProcess(rentedСar);
        final Rental rental = rentalMapper.createDtoToEntity(requestDto);
        rental.setCar(rentedСar);
        rental.setUser((User) authentication.getPrincipal());
        rental.setActive(true);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    private void carInventoryProcess(Car rentedСar) {
        final Integer carQuantity = rentedСar.getInventory();
        if (carQuantity == 0){
            throw new NullPointerException("These car models are out of stock for rent");
        }
        rentedСar.setInventory(carQuantity - 1);
        carRepository.save(rentedСar);
    }
}
