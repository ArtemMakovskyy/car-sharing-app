package com.personal.carsharing.carsharingapp.service.impl;

import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.repository.user.UserRepository;
import com.personal.carsharing.carsharingapp.service.NotificationService;
import com.personal.carsharing.carsharingapp.service.api.telegram.bot.TelegramBotManager;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private static final Logger logger = LogManager.getLogger(TelegramNotificationService.class);
    private final TelegramBotManager telegramBotManager;
    private final UserRepository userRepository;

    @Override
    public void sendNotification(String message, Long recipientId) {
        if (recipientId != null) {
            final User user = userRepository.findById(recipientId).orElseThrow(
                    () -> new EntityNotFoundException("Can't find user by id " + recipientId));
            if (user.getTelegramChatId() == null) {
                logger.debug("User with id " + user.getTelegramChatId()
                        + " doesn't have telegram ID. User should login "
                        + "in Bot to getting Telegram notification.");
                return;
            }
            message = "API NOTIFICATION:\n" + message;
            telegramBotManager.sendMessageFromApiToChat(user.getTelegramChatId(), message);
        }
    }
}
