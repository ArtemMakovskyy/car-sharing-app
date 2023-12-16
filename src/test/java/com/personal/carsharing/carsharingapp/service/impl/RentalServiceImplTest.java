package com.personal.carsharing.carsharingapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.model.Car;
import com.personal.carsharing.carsharingapp.model.CarType;
import com.personal.carsharing.carsharingapp.model.Rental;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.repository.car.CarRepository;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.service.NotificationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private NotificationService notificationService;

    @DisplayName("Save rental by valid data and return rentalDto")
    @Test
    void add_ValidData_ReturnRentalDto() {
        //given
        CreateRentalRequestDto requestDto = getCreateRentalRequestDto();
        User user = getUser();
        Car rentedCar = getCar();
        Rental rental = getRental(rentedCar, user, requestDto);
        RentalDto expectedRentalDto = toRentalDto(rental);

        when(carRepository.findById(requestDto.getCarId())).thenReturn(Optional.of(rentedCar));
        when(rentalMapper.createDtoToEntity(requestDto)).thenReturn(rental);
        when(carRepository.save(rentedCar)).thenReturn(rentedCar);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expectedRentalDto);
        when(rentalRepository.findAllByUserIdAndActive(user.getId(), true, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(notificationService.sendNotification(anyString(), anyLong())).thenReturn(false);

        //when
        RentalDto actualRentalDto = rentalService.add(requestDto, user);
        //then
        assertNotNull(actualRentalDto);
        assertEquals(expectedRentalDto.getId(), actualRentalDto.getId());
        verify(carRepository, times(1)).findById(requestDto.getCarId());
        verify(rentalMapper, times(1)).createDtoToEntity(requestDto);
        verify(carRepository, times(1)).save(rentedCar);
        verify(rentalRepository, times(1)).save(rental);
        verify(rentalMapper, times(1)).toDto(rental);
        verify(notificationService, times(1)).sendNotification(anyString(), anyLong());
    }

    @DisplayName("""
            Find all rentals by valid user ID and status is active. 
            Return List with one RentalDto """)
    @Test
    void findAllByUserIdAndStatus_UserIdProvided_ReturnRentalDtoList() {

        // given
        Long userId = getUser().getId();
        final Rental rental = getRental(getCar(), getUser(), getCreateRentalRequestDto());

        List<RentalDto> expectedRentalDtos = Collections.singletonList(toRentalDto(rental));

        when(rentalRepository.findAllByUserIdAndActive(getUser().getId(), true, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Collections.singletonList(rental)));

        // when
        List<RentalDto> actualRentalDtos = rentalService
                .findAllByUserIdAndStatus(userId, true, Pageable.unpaged());

        // then
        assertNotNull(actualRentalDtos);
        assertEquals(expectedRentalDtos.size(), actualRentalDtos.size());
        verify(rentalRepository, times(1))
                .findAllByUserIdAndActive(getUser().getId(), true, Pageable.unpaged());
        verify(rentalMapper, times(1)).toDto(any(Rental.class));
    }

    @DisplayName("Find all rentals by valid user ID is null. Return List with one RentalDto")
    @Test
    void findAllByUserIdAndStatus_UserIdIsNull_ReturnRentalDtoList() {

        // given
        Long userId = null;

        final Rental rental = getRental(getCar(), getUser(), getCreateRentalRequestDto());
        List<RentalDto> expectedRentalDtos = Collections.singletonList(toRentalDto(rental));

        when(rentalRepository.findByIsActive(true, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Collections.singletonList(rental)));

        // when
        List<RentalDto> actualRentalDtos =
                rentalService.findAllByUserIdAndStatus(
                        userId, true, Pageable.unpaged());

        // then
        assertNotNull(actualRentalDtos);
        assertEquals(expectedRentalDtos.size(), actualRentalDtos.size());
        verify(rentalRepository, times(1)).findByIsActive(true, Pageable.unpaged());
        verify(rentalMapper, times(1)).toDto(any(Rental.class));
    }

    private RentalDto toRentalDto(Rental rental) {
        RentalDto rentalDto = new RentalDto();
        rentalDto.setId(rental.getId());
        rentalDto.setRentalDate(rental.getRentalDate());
        rentalDto.setReturnDate(rental.getReturnDate());
        rentalDto.setCarId(rental.getCar().getId());
        rentalDto.setUserId(rental.getUser().getId());
        return rentalDto;
    }

    private Rental getRental(Car car, User user, CreateRentalRequestDto requestDto) {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(requestDto.getRentalDate());
        rental.setReturnDate(requestDto.getReturnDate());
        rental.setCar(car);
        rental.setUser(user);
        rental.setDeleted(false);
        rental.setActive(true);
        return rental;
    }

    private User getUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("user1234@email.com");
        user.setFirstName("UserFirstName");
        user.setLastName("UserLastName");
        user.setPassword("1234");
        user.setDeleted(false);
        return user;
    }

    private Car getCar() {
        Car car = new Car();
        car.setId(5L);
        car.setModel("Camry");
        car.setBrand("Toyota");
        car.setType(CarType.SEDAN);
        car.setInventory(1);
        car.setDailyFee(BigDecimal.valueOf(25.50));
        car.setDeleted(false);
        return car;
    }

    private CreateRentalRequestDto getCreateRentalRequestDto() {
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto();
        requestDto.setRentalDate(LocalDate.of(2099, 06, 9));
        requestDto.setReturnDate(LocalDate.of(2099, 07, 9));
        requestDto.setCarId(5L);
        return requestDto;
    }
}
