package com.personal.carsharing.carsharingapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;
import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.exception.StripeProcessException;
import com.personal.carsharing.carsharingapp.model.Role;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.service.PaymentService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("Customer requests payments and controller uses customer id")
    void getPaymentsById_CustomerRole_UsesPrincipalId() throws Exception {
        PaymentResponseDto responseDto = paymentResponseDto();
        when(paymentService.findAllByRentalUserId(eq(2L), any(Pageable.class)))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/payments/")
                        .with(user(userWithRole(2L, Role.RoleName.ROLE_CUSTOMER)))
                        .param("user_id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rentalId").value(2L));

        verify(paymentService, times(1)).findAllByRentalUserId(eq(2L), any(Pageable.class));
    }

    @Test
    @DisplayName("Admin requests payments and controller uses query user_id")
    void getPaymentsById_AdminRole_UsesQueryUserId() throws Exception {
        PaymentResponseDto responseDto = paymentResponseDto();
        when(paymentService.findAllByRentalUserId(eq(999L), any(Pageable.class)))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/payments/")
                        .with(user(userWithRole(1L, Role.RoleName.ROLE_ADMIN)))
                        .param("user_id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(paymentService, times(1)).findAllByRentalUserId(eq(999L), any(Pageable.class));
    }

    @Test
    @DisplayName("Create payment session with invalid payload returns 400")
    void createPaymentSession_InvalidPayload_ReturnsBadRequest() throws Exception {
        String invalidPayload = "{\"rentalId\":1,\"paymentType\":\"\"}";

        mockMvc.perform(post("/payments/")
                        .with(user(org.springframework.security.core.userdetails.User
                                .withUsername("auth-user")
                                .password("1234")
                                .roles("ADMIN")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Create payment session delegates to service")
    void createPaymentSession_ValidPayload_ReturnsPaymentResponse() throws Exception {
        CreatePaymentSessionDto requestDto = new CreatePaymentSessionDto(2L, "PAYMENT");
        PaymentResponseDto responseDto = paymentResponseDto();
        when(paymentService.createPaymentSession(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/payments/")
                        .with(user(org.springframework.security.core.userdetails.User
                                .withUsername("auth-user")
                                .password("1234")
                                .roles("ADMIN")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-1"));
    }

    @Test
    @DisplayName("Unknown payment session in success callback returns 404 contract")
    void handleSuccessfulPayment_UnknownSession_ReturnsNotFoundContract() throws Exception {
        when(paymentService.handleSuccessfulPayment("unknown-session"))
                .thenThrow(new EntityNotFoundException(
                        "Can't find payment by session ID unknown-session"));

        mockMvc.perform(get("/payments/success")
                        .with(user(org.springframework.security.core.userdetails.User
                                .withUsername("auth-user")
                                .password("1234")
                                .roles("ADMIN")
                                .build()))
                        .param("session_id", "unknown-session"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(
                        "Entity not found. Can't find payment by session ID unknown-session"));
    }

    @Test
    @DisplayName("Stripe processing failure in cancel callback returns 404 contract")
    void processPaymentCancellation_StripeFailure_ReturnsNotFoundContract() throws Exception {
        when(paymentService.processPaymentCancellation("failed-session"))
                .thenThrow(new StripeProcessException(
                        "Can't get expiration time", new RuntimeException()));

        mockMvc.perform(get("/payments/cancel")
                        .with(user(org.springframework.security.core.userdetails.User
                                .withUsername("auth-user")
                                .password("1234")
                                .roles("ADMIN")
                                .build()))
                        .param("session_id", "failed-session"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(
                        "Stripe Process Exception. Can't get expiration time"));
    }

    private User userWithRole(Long id, Role.RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);
        User user = new User();
        user.setId(id);
        user.setEmail("user@email.com");
        user.setPassword("1234");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setRoles(Set.of(role));
        return user;
    }

    private PaymentResponseDto paymentResponseDto() {
        return new PaymentResponseDto(
                "payment-1",
                "PENDING",
                "PAYMENT",
                2L,
                "session-1",
                2200,
                "https://session.url");
    }
}
