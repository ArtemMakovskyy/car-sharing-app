package com.personal.carsharing.carsharingapp.service.api.telegram;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TelegramBotCredentialProvider {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String token;

    @Value("${telegram.admin.bot.name}")
    private String adminBotName;
    @Value("${telegram.admin.bot.token}")
    private String adminToken;

    @Value("${bot.name.recipe}")
    private String recipeBotName;
    @Value("${bot.token.recipe}")
    private String recipeToken;
}
