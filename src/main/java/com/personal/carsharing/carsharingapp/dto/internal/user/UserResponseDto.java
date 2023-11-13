package com.personal.carsharing.carsharingapp.dto.internal.user;

import java.util.Set;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}
