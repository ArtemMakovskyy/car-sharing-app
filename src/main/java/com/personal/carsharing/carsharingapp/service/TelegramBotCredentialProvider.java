package com.personal.carsharing.carsharingapp.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
//@ConfigurationProperties
public class TelegramBotCredentialProvider {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String token;
}
