package com.personal.carsharing.carsharingapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserLoginRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserLoginResponseDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserRegistrationRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.exception.RegistrationException;
import com.personal.carsharing.carsharingapp.security.AuthenticationService;
import com.personal.carsharing.carsharingapp.service.UserService;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Register user with valid payload returns UserResponseDto")
    void registerUser_ValidPayload_ReturnsUserResponseDto() throws Exception {
        UserRegistrationRequestDto requestDto = registrationRequest();
        UserResponseDto responseDto = userResponseDto();
        when(userService.register(any(UserRegistrationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.email").value(responseDto.getEmail()));

        verify(userService, times(1)).register(any(UserRegistrationRequestDto.class));
    }

    @Test
    @DisplayName("Register user with invalid payload returns 400")
    void registerUser_InvalidPayload_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("bad");
        requestDto.setFirstName("");
        requestDto.setLastName("");
        requestDto.setPassword("12");
        requestDto.setRepeatPassword("21");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Register duplicate user returns formatted registration error")
    void registerUser_DuplicateUser_ReturnsRegistrationError() throws Exception {
        UserRegistrationRequestDto requestDto = registrationRequest();
        when(userService.register(any(UserRegistrationRequestDto.class)))
                .thenThrow(new RegistrationException("Unable to complete registration."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Registration failed. Unable to complete registration."));
    }

    @Test
    @DisplayName("Login user with valid payload returns token")
    void loginUser_ValidPayload_ReturnsJwtToken() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("user12@email.com", "1234");
        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenReturn(new UserLoginResponseDto("jwt-token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    private UserRegistrationRequestDto registrationRequest() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("customer@email.com");
        requestDto.setFirstName("First");
        requestDto.setLastName("Last");
        requestDto.setPassword("1234");
        requestDto.setRepeatPassword("1234");
        return requestDto;
    }

    private UserResponseDto userResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setEmail("customer@email.com");
        userResponseDto.setFirstName("First");
        userResponseDto.setLastName("Last");
        userResponseDto.setRoles(Set.of("ROLE_CUSTOMER"));
        return userResponseDto;
    }
}
