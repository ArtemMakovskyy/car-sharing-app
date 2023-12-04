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
import com.personal.carsharing.carsharingapp.service.NotificationService;
import com.personal.carsharing.carsharingapp.service.RentalService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
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
    private final NotificationService notificationService;

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

        notificationService.sendNotification(
                String.format("You recently got rent car: %s %s%nReturn date: %s",
                        rentedСar.getBrand(), rentedСar.getModel(),
                        changeDateFormat(rental.getReturnDate().toString())), user.getId());

        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalDto> findAllByUserIdAndStatus(
            Long userId, Boolean isActive, Pageable pageable) {
        if (userId == null) {
            return rentalRepository.findByIsActive(isActive, pageable)
                    .stream()
                    .map(rentalMapper::toDto)
                    .toList();
        }
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
                        "You can't return a car that you did not rent"));
        rental.setActualReturnDate(LocalDate.now());
        rental.setActive(false);
        rentalRepository.save(rental);
        final Car car = carRepository.findById(rental.getCar().getId()).orElseThrow(
                () -> new EntityNotFoundException("Can't get car by id "
                        + rental.getCar().getId()));
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        notificationService.sendNotification(
                "You recently returned rent car ", user.getId());
        return rentalMapper.toDto(rental);
    }

    public static String changeDateFormat(String dateStringWithDash) {
        String patternWithDash = "yyyy-MM-dd";
        String patternWithSlash = "yyyy/MM/dd";
        SimpleDateFormat dateFormatWithDash = new SimpleDateFormat(patternWithDash);
        SimpleDateFormat dateFormatWithSlash = new SimpleDateFormat(patternWithSlash);
        try {
            Date date = dateFormatWithDash.parse(dateStringWithDash);
            return dateFormatWithSlash.format(date);
        } catch (ParseException e) {
            throw new RuntimeException("Can't parse data " + e);
        }
    }

    private boolean checkingDuplicateCarRental(Long userId) {
        final List<RentalDto> rentalDtos = rentalRepository.findAllByUserIdAndActive(
                        userId, true, Pageable.unpaged()).stream()
                .map(rentalMapper::toDto).toList();
        if (rentalDtos.isEmpty()) {
            return true;
        }
        throw new DataDuplicationException("You can't rent two cars at the same time");
    }
}
