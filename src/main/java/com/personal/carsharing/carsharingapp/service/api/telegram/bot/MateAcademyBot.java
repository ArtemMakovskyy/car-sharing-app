package com.personal.carsharing.carsharingapp.service.api.telegram.bot;

import com.personal.carsharing.carsharingapp.service.api.telegram.TelegramBotCredentialProvider;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class MateAcademyBot extends TelegramLongPollingBot {
    private final TelegramBotCredentialProvider credentialProvider;

    @Override
    public String getBotUsername() {
        return credentialProvider.getRecipeBotName();
    }

    @Override
    public String getBotToken() {
        return credentialProvider.getRecipeToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Hello user I receive your massage "
                + message.getText().toUpperCase());
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        if (message.getText().equals("/start")) {
            String text = "Welcome to recipe bot! Please pass the meat of the day!\n";
            //            text += "/breakfast\n";
            //            text += "/dinner\n";
            //            text += "/lunch\n";
            //            text += "/supper\n";

            sendMessage.enableMarkdown(true);
            ReplyKeyboardMarkup keyboardMarkup = getMenuKeyboard();
            sendMessage.setReplyMarkup(keyboardMarkup);
            sendMessage.setText(text);
        }

        if (message.getText().equals("breakfast") || message.getText().equals("/breakfast")) {
            String menu = "Breakfast menu!\n";
            menu = menu + "1. BVlueberry-Banana-Nut Smoothie\n";
            menu = menu + "2. Classic Omelet and Greens\n";
            menu = menu + "3. Curry-Avocado Crispy Egg Toast\n";
            sendMessage.setText(menu);
        }
        if (message.getText().equals("dinner") || message.getText().equals("/dinner")) {
            String menu = "Dinner menu!\n";
            menu = menu + "1. Creamy Lemon Chicken Pasta\n";
            menu = menu + "2. Turkey Tacos\n";
            menu = menu + "3. Vegetarian Lasagna\n";
            sendMessage.setText(menu);
        }
        if (message.getText().equals("lunch") || message.getText().equals("/lunch")) {
            String menu = "Lunch menu is in progress...\n";
            sendMessage.setText(menu);
        }
        if (message.getText().equals("supper") || message.getText().equals("/supper")) {
            String menu = "Supper menu is in progress...\n";
            sendMessage.setText(menu);
        }
        try {
            execute(sendMessage);
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
        keyboardRow.add("breakfast");
        keyboardRow.add("dinner");
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add("lunch");
        keyboardSecondRow.add("supper");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardSecondRow);

        rkm.setKeyboard(keyboardRows);
        return rkm;
    }
}
