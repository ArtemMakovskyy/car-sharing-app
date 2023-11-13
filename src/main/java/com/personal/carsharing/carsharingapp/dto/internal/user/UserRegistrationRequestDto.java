package com.personal.carsharing.carsharingapp.dto.internal.user;

import com.personal.carsharing.carsharingapp.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords do not match!"
)
public class UserRegistrationRequestDto {
    @Email(regexp = ".{5,20}@(\\S+)$",
            message = "length must be from 5 characters to 20 before @")
    private String email;
    @NotBlank(message = "must be non blank")
    private String firstName;
    @NotBlank(message = "must be non blank")
    private String lastName;
    @Size(min = 4, max = 20, message = "must be from 4 to 20 characters")
    private String password;
    private String repeatPassword;
}
