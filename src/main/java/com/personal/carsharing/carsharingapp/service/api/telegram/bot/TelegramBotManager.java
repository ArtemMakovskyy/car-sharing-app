package com.personal.carsharing.carsharingapp.service.api.telegram.bot;

import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseWithChatIdDto;
import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.dto.mapper.UserMapper;
import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.model.Role;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.repository.role.RoleRepository;
import com.personal.carsharing.carsharingapp.repository.user.UserRepository;
import com.personal.carsharing.carsharingapp.service.CarService;
import com.personal.carsharing.carsharingapp.service.api.telegram.TelegramBotCredentialProvider;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBotManager extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(TelegramBotManager.class);
    private final TelegramBotCredentialProvider credentialProvider;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final UserMapper userMapper;
    private final RentalMapper rentalMapper;
    private final CarService carService;
    private final RoleRepository roleRepository;

    @Override
    public String getBotUsername() {
        return credentialProvider.getBotName();
    }

    @Override
    public String getBotToken() {
        return credentialProvider.getToken();
    }

    public void sendMessageFromApiToChat(
            Long chatId,
            String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*" + textMessage + "*");
        sendMessage.setParseMode("MarkdownV2");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String textFromUSer = update.getMessage().getText();
            Long userChatId = update.getMessage().getChatId();
            switch (textFromUSer) {
                case "/start", "Start application" -> startCommandReceived(
                        userChatId, update.getMessage().getChat().getFirstName());
                case "/user_current_rental", "Current Rental" ->
                        currentRentalCommandReceived(userChatId);
                case "/exit", "Log out" -> exitRentalsCommandReceived(userChatId);
                case "/help", "Help" -> helpRentalsCommandReceived(userChatId);
                default -> processTextMessage(userChatId, update.getMessage().getText());
            }
        }
    }

    private void startCommandReceived(Long chatId, String firstName) {
        String message = firstName + """
                , welcome to the Car Sharing Bot!
                For identity, input your email and press "enter",         
                or wright down your question administrator will call you after process.
                """;
        sendMessageToChat(chatId, message, getRegisterButtons());
    }

    private void exitRentalsCommandReceived(Long chatId) {
        final User user = userRepository.findByTelegramChatId(chatId)
                .stream()
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user by chat ID " + chatId));
        user.setTelegramChatId(null);
        userRepository.save(user);
        String message = "You are logged out.";
        sendMessageToChat(chatId, message, getRegisterButtons());
    }

    @Transactional
    private void currentRentalCommandReceived(Long chatId) {
        Long userId = userRepository.findByTelegramChatId(chatId)
                .stream()
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user by chat ID " + chatId))
                .getId();
        try {
            RentalDto rentalDto = rentalMapper.toDto(rentalRepository
                    .findAllByUserIdAndActive(userId, true, Pageable.unpaged())
                    .stream()
                    .findAny()
                    .orElseThrow(EntityNotFoundException::new));
            final CarDto carById = carService.findById(rentalDto.getCarId());
            String message = String.format(
                    "You are using %s %s%n", carById.brand(), carById.model())
                    + String.format("rented on the: %s%n", rentalDto.getRentalDate())
                    + String.format("time to return on the: %s%n", rentalDto.getReturnDate());
            sendMessageToChat(chatId, message, getWorkButtons());
        } catch (EntityNotFoundException nfe) {
            sendMessageToChat(chatId, "You don't have a rent car", getWorkButtons());
        } catch (Exception e) {
            logger.error("An error occurred in currentRentalCommandReceived", e);
            sendMessageToChat(chatId,
                    "An error occurred, please try again later", getWorkButtons());
        }
    }

    private void helpRentalsCommandReceived(Long chatId) {
        String helpMessage = """ 
                Chat bot features: 
                1. /start: for starting application. 
                2. /my_current_rental: displays current car rental and rental details. 
                3. /help: displays a list of functions.
                4. /exit: Log out. """;

        if (userRepository.findByTelegramChatId(chatId).isEmpty()) {
            sendMessageToChat(chatId, helpMessage, getRegisterButtons());
        } else {
            sendMessageToChat(chatId, helpMessage, getWorkButtons());
        }
    }

    private void processTextMessage(Long chatId, String emailOrMessageToAdmin) {
        if (isValidEmail(emailOrMessageToAdmin)) {
            String email = emailOrMessageToAdmin;
            addUserToChat(chatId, email);
        } else {
            String messageToAdmin = emailOrMessageToAdmin;
            sendQuestionMessageToAdmin(chatId, messageToAdmin);
        }
    }

    private void sendQuestionMessageToAdmin(Long chatId, String messageToAnAdmin) {
        final Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("ROLE_ADMIN not found"));
        final UserResponseWithChatIdDto userResponseWithChatIdDto =
                userRepository.findByRoles(adminRole)
                        .stream()
                        .map(userMapper::toDtoWithChatId)
                        .findAny()
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Can't get admin by role " + adminRole.getName()));
        if (userResponseWithChatIdDto.getTelegramChatId() == null) {
            sendMessageToChat(chatId,
                    "We can't process your message because "
                           + "is no administrator at the moment, please try again later",
                    getWorkButtons());
        } else {
            sendMessageToChat(chatId,
                    "The administrator will process the message then contact you",
                    getWorkButtons());
            sendMessageToChat(userResponseWithChatIdDto.getTelegramChatId(),
                    messageToAnAdmin, getWorkButtons());
        }
    }

    private void addUserToChat(Long chatId, String emailOrMessageToAdmins) {
        Optional<User> optionalUserByEmail = userRepository.findUserByEmail(emailOrMessageToAdmins);
        if (optionalUserByEmail.isPresent()) {
            if (isExistEmailRegistration(chatId, optionalUserByEmail)) {
                return;
            }
            if (isUseMultipleEmailsBySingleChat(chatId)) {
                return;
            }
            User user = optionalUserByEmail.get();
            user.setTelegramChatId(chatId);
            userRepository.save(user);
            sendMessageToChat(chatId,
                    "User registration successful", getWorkButtons());
        } else {
            sendMessageToChat(chatId,
                    "Сan`t find a user by email.", getRegisterButtons());
        }
    }

    private boolean isUseMultipleEmailsBySingleChat(Long chatId) {
        if (userRepository.findByTelegramChatId(chatId).size() > 0) {
            sendMessageToChat(chatId, """
                    You cannot register with multiple email addresses at the same time.
                    Log out with previous email, then register a new one.""", getWorkButtons());
            return true;
        }
        return false;
    }

    private boolean isExistEmailRegistration(Long chatId, Optional<User> optionalUserByEmail) {
        if (Objects.equals(optionalUserByEmail.get().getTelegramChatId(), chatId)) {
            sendMessageToChat(chatId, "You are already registered", getWorkButtons());
            return true;
        }
        return false;
    }

    private void sendMessageToChat(
            Long chatId,
            String textMessage,
            ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboardMarkup getWorkButtons() {
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("Current Rental");
        firstRow.add("Log out");
        firstRow.add("Help");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(firstRow);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup getRegisterButtons() {
        KeyboardRow loginLineButtons = new KeyboardRow();
        loginLineButtons.add("Start application");
        loginLineButtons.add("Help");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(loginLineButtons);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
