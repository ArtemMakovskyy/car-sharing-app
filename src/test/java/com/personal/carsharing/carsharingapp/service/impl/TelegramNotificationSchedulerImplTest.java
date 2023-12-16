package com.personal.carsharing.carsharingapp.service.impl;

import static org.mockito.Mockito.when;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDetailedResponseDto;
import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.model.Car;
import com.personal.carsharing.carsharingapp.model.CarType;
import com.personal.carsharing.carsharingapp.model.Rental;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.service.NotificationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationSchedulerImplTest {

    @InjectMocks
    private TelegramNotificationSchedulerImpl telegramNotificationScheduler;

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private NotificationService notificationService;

    @Test
    void dailyMessagesAboutCarRentalReturnStatus_shouldNotSendNotifications() {
        // given
        RentalDetailedResponseDto rentalDto = new RentalDetailedResponseDto();
        rentalDto.setReturnDate(LocalDate.now().minusDays(1));

        when(rentalRepository.findAllDetailedRentalsWithTelegramChatId())
                .thenReturn(getRentals());
        when(rentalMapper.toDetailedDto(Mockito.any()))
                .thenReturn(rentalDto);

        // when
        telegramNotificationScheduler.dailyMessagesAboutCarRentalReturnStatus();

        // then
        Mockito.verify(notificationService, Mockito.never())
                .sendNotification(Mockito.anyString(), Mockito.anyLong());
    }

    private List<Rental> getRentals() {
        return List.of(
                getRental(getCarExplorer(), getFirstUser(), getCreateRentalRequestDto())
        );
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

    private User getFirstUser() {
        User user = new User();
        user.setId(3L);
        user.setEmail("user2234@email.com");
        user.setFirstName("SecondUserFirstName");
        user.setLastName("SecondUserLastName");
        user.setPassword("1234");
        user.setTelegramChatId(22222L);
        user.setDeleted(false);
        return user;
    }

    private Car getCarExplorer() {
        Car car = new Car();
        car.setId(6L);
        car.setModel("Explorer");
        car.setBrand("Ford");
        car.setType(CarType.SUV);
        car.setInventory(1);
        car.setDailyFee(BigDecimal.valueOf(25.99));
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
