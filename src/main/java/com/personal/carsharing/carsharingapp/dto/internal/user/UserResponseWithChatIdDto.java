package com.personal.carsharing.carsharingapp.dto.internal.user;

import lombok.Data;

@Data
public class UserResponseWithChatIdDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Long telegramChatId;
}
