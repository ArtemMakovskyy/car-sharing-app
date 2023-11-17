package com.personal.carsharing.carsharingapp.service.api.telegram.bot;

import com.personal.carsharing.carsharingapp.service.api.telegram.TelegramBotCredentialProvider;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class AdminTelegramBot extends TelegramLongPollingBot {
    private final TelegramBotCredentialProvider credentialProvider;

    @Override
    public void onUpdateReceived(Update update) {
        Message message2 = update.getMessage();
        System.out.println("Massage received " + message2.getText());

        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(update.getMessage().getText());
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (update.hasMessage() && update.getMessage().getChat().isUserChat()) {
            sendMenu(update.getMessage().getChatId());
        }
    }

    @Override
    public String getBotUsername() {

        return credentialProvider.getAdminBotName();
    }

    @Override
    public String getBotToken() {
        return credentialProvider.getAdminToken();
    }

    private void sendMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Welcome! Please select an option:");
        message.setReplyMarkup(getMenuKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getMenuKeyboard() {
        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
        rkm.setSelective(true);
        rkm.setResizeKeyboard(true);
        rkm.setOneTimeKeyboard(false);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("Start working with the bot");
        keyboardRow.add("Sign in");
        keyboardRow.add("Show current rental");

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add("Show list of all rentals");
        keyboardSecondRow.add("Instructions for use");
        keyboardSecondRow.add("Sign out");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardSecondRow);

        rkm.setKeyboard(keyboardRows);
        return rkm;
    }
}
