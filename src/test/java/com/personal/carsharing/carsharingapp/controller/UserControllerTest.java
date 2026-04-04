package com.personal.carsharing.carsharingapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.carsharing.carsharingapp.dto.internal.user.UpdateUserRoleDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserRegistrationRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.service.UserService;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Update user role with admin authority returns updated user")
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_AdminAuthority_ReturnsUpdatedUser() throws Exception {
        Long userId = 7L;
        UpdateUserRoleDto roleDto = new UpdateUserRoleDto("ROLE_MANAGER");
        UserResponseDto responseDto = response(userId, "ROLE_MANAGER");
        when(userService.updateRole(userId, "ROLE_MANAGER")).thenReturn(responseDto);

        mockMvc.perform(put("/users/{id}/role", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.roles").isArray());

        verify(userService, times(1)).updateRole(userId, "ROLE_MANAGER");
    }

    @Test
    @DisplayName("Update user role with invalid payload returns 400")
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_InvalidPayload_ReturnsBadRequest() throws Exception {
        Long userId = 8L;
        UpdateUserRoleDto roleDto = new UpdateUserRoleDto("WRONG_ROLE");

        mockMvc.perform(put("/users/{id}/role", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Get current profile as customer returns UserResponseDto")
    @WithMockUser(roles = "CUSTOMER")
    void getMyProfile_CustomerAuthority_ReturnsProfile() throws Exception {
        UserResponseDto responseDto = response(2L, "ROLE_CUSTOMER");
        when(userService.getUserFromAuthentication(any())).thenReturn(responseDto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@email.com"));

        verify(userService, times(1)).getUserFromAuthentication(any());
    }

    @Test
    @DisplayName("Update current profile with invalid data returns 400")
    @WithMockUser(roles = "CUSTOMER")
    void updateUserProfile_InvalidPayload_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("bad");
        requestDto.setFirstName("");
        requestDto.setLastName("");
        requestDto.setPassword("1");
        requestDto.setRepeatPassword("2");

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    private UserResponseDto response(Long id, String role) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(id);
        responseDto.setEmail("user@email.com");
        responseDto.setFirstName("First");
        responseDto.setLastName("Last");
        responseDto.setRoles(Set.of(role));
        return responseDto;
    }
}
