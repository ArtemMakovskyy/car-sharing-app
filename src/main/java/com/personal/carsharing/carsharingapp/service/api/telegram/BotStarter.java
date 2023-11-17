package com.personal.carsharing.carsharingapp.service.api.telegram;

import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.repository.user.UserRepository;
import com.personal.carsharing.carsharingapp.service.CarService;
import com.personal.carsharing.carsharingapp.service.api.telegram.bot.AdminTelegramBot;
import com.personal.carsharing.carsharingapp.service.api.telegram.bot.MateAcademyBot;
import com.personal.carsharing.carsharingapp.service.api.telegram.bot.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class BotStarter {
    private final TelegramBot telegramBot;
    private final TelegramBotCredentialProvider credentialProvider;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarService carService;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new AdminTelegramBot(credentialProvider));
            botsApi.registerBot(new MateAcademyBot(credentialProvider));
            botsApi.registerBot(
                    new TelegramBot(credentialProvider, userRepository,
                            rentalRepository, rentalMapper, carService));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
