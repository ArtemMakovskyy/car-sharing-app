package com.personal.carsharing.carsharingapp.service.impl;

import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDetailedResponseDto;
import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.service.NotificationScheduler;
import com.personal.carsharing.carsharingapp.service.NotificationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class TelegramNotificationSchedulerImpl implements NotificationScheduler {
    private static final String EVERY_DEY_AT_TEN_OCKLOCK = "0 0 10 * * ?";
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final NotificationService notificationService;

    @Override
    @Scheduled(cron = EVERY_DEY_AT_TEN_OCKLOCK)
    public void dailyMessagesAboutCarRentalReturnStatus() {
        final List<RentalDetailedResponseDto> rentalDetailedResponseDtos =
                rentalRepository.findAllDetailedRentalsWithTelegramChatId()
                        .stream()
                        .map(rentalMapper::toDetailedDto)
                        .toList();
        for (RentalDetailedResponseDto rentalDto : rentalDetailedResponseDtos) {
            if (rentalDto.getReturnDate().minusDays(1).isBefore(LocalDate.now())) {
                notificationService.sendNotification(rentalDto.getUserFirstName() + ", return"
                        + " date is tomorrow or earlier, and the car is still not"
                        + " returned", rentalDto.getUserId());
            } else {
                notificationService.sendNotification(rentalDto.getUserFirstName()
                        + ", no rentals overdue today", rentalDto.getUserId());
            }
        }
    }
}
