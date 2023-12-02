package com.personal.carsharing.carsharingapp.service;

import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.dto.mapper.UserMapper;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.repository.role.RoleRepository;
import com.personal.carsharing.carsharingapp.repository.user.UserRepository;
import com.personal.carsharing.carsharingapp.service.impl.TelegramBotNotificationService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
@Getter
public class TelegramBotStarter {
    private final TelegramBotCredentialProvider credentialProvider;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final UserMapper userMapper;
    private final RentalMapper rentalMapper;
    private final CarService carService;
    private final RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(
                    new TelegramBotNotificationService(
                            credentialProvider, userRepository, rentalRepository,
                            userMapper, rentalMapper, carService, roleRepository));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
