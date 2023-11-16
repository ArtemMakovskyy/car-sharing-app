package com.personal.carsharing.carsharingapp.service.impl;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.exception.DataDuplicationException;
import com.personal.carsharing.carsharingapp.exception.EmptyDataException;
import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.model.Car;
import com.personal.carsharing.carsharingapp.model.Rental;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.repository.car.CarRepository;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.service.RentalService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Override
    public RentalDto add(
            CreateRentalRequestDto requestDto,
            Authentication authentication) {
        final Car rentedСar = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id " + requestDto.getCarId()));
        if (rentedСar.getInventory() == 0) {
            throw new EmptyDataException("These car models are out of stock for rent");
        }
        rentedСar.setInventory(rentedСar.getInventory() - 1);

        final User user = (User) authentication.getPrincipal();
        checkingDuplicateCarRental(user.getId());

        final Rental rental = rentalMapper.createDtoToEntity(requestDto);
        rental.setCar(rentedСar);
        rental.setUser(user);
        rental.setActive(true);
        carRepository.save(rentedСar);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalDto> findAllByUserIdAndStatus(
            Long userId, Boolean isActive, Pageable pageable) {
        return rentalRepository.findAllByUserIdAndActive(userId, isActive, pageable)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalDto getUserRentalDetailsByAuthentication(Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return rentalRepository.findAllByUserIdAndActive(user.getId(), true, Pageable.unpaged())
                .stream()
                .map(rentalMapper::toDto)
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find rent by id " + user.getId()));
    }

    @Override
    public RentalDto returnRentalCar(Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        final Rental rental = rentalRepository.findAllByUserIdAndActive(
                        user.getId(), true, Pageable.unpaged())
                .stream()
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        "You cannot return a car that you did not rent"));
        rental.setActualReturnDate(LocalDateTime.now());
        rental.setActive(false);
        rentalRepository.save(rental);
        final Car car = carRepository.findById(rental.getCar().getId()).get();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        return rentalMapper.toDto(rental);
    }

    private boolean checkingDuplicateCarRental(Long userId) {
        final List<RentalDto> rentalDtos = rentalRepository.findAllByUserIdAndActive(
                        userId, true, Pageable.unpaged()).stream()
                .map(rentalMapper::toDto).toList();
        if (rentalDtos.isEmpty()) {
            return true;
        }
        throw new DataDuplicationException("You cannot rent two cars at the same time");
    }
}
