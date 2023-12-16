package com.personal.carsharing.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.model.Role;
import com.personal.carsharing.carsharingapp.model.User;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerTest {
    protected static MockMvc mockMvc;
    private static final Long EXPECTED_CAR_DTO_ID = 1L;
    private static final Long EXPECTED_RENTAL_DTO_ID = 4L;
    private static final Long USER_ID = 6L;
    private static final Long CAR_ID = 9L;
    private static final int ADD_DAYS_TO_CREATE_RENTAL_DATE = 1;
    private static final int ADD_DAYS_TO_CREATE_RETURN_DATE = 8;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @DisplayName("Create new rental with valid rental. Return RentalDto")
    @Sql(scripts = {
            "classpath:database/user/insert-four-users-into-user-table.sql",
            "classpath:database/user/insert-users_roles-into-users_roles-table.sql",
            "classpath:database/car/insert-cars-into-car-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/delete-all-from-rentals-table.sql",
            "classpath:database/user/delete-four-users-from-users-table.sql",
            "classpath:database/car/delete-three-cars-from-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addRental_ValidRental_ReturnRentalDto() throws Exception {
        //given
        User user = getUser();
        CreateRentalRequestDto requestDto = getCreateRentalRequestDto(CAR_ID);

        mockMvc.perform(
                        post("/rentals")
                                .with(user(user))
                                .content(objectMapper.writeValueAsString(requestDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.carId").value(CAR_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andReturn();
    }

    @Test
    @Sql(scripts = {
            "classpath:database/user/insert-four-users-into-user-table.sql",
            "classpath:database/user/insert-users_roles-into-users_roles-table.sql",
            "classpath:database/car/insert-cars-into-car-table.sql",
            "classpath:database/rentals/insert-rentals-into-rental-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/delete-all-from-rentals-table.sql",
            "classpath:database/user/delete-four-users-from-users-table.sql",
            "classpath:database/car/delete-three-cars-from-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get all rentals by user ID and rental status. Return list of RentalDto")
    void getRentalsByUserIdAndRentalStatus_Valid_ReturnRentalDtos() throws Exception {
        //given
        User user = getUser();
        mockMvc.perform(
                        get("/rentals/")
                                .with(user(user))
                                .param("user_id", USER_ID.toString())
                                .param("is_active", "true")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EXPECTED_RENTAL_DTO_ID))
                .andExpect(jsonPath("$[0].carId").value(EXPECTED_CAR_DTO_ID))
                .andExpect(jsonPath("$[0].userId").value(USER_ID))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @DisplayName("Get current user rental detail. Return RentalDto")
    @Sql(scripts = {
            "classpath:database/user/insert-four-users-into-user-table.sql",
            "classpath:database/user/insert-users_roles-into-users_roles-table.sql",
            "classpath:database/car/insert-cars-into-car-table.sql",
            "classpath:database/rentals/insert-rentals-into-rental-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/delete-all-from-rentals-table.sql",
            "classpath:database/user/delete-four-users-from-users-table.sql",
            "classpath:database/car/delete-three-cars-from-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUserRentalDetails_Valid_ReturnRentalDto() throws Exception {
        User user = getUser();
        mockMvc.perform(
                        get("/rentals")
                                .with(user(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(EXPECTED_RENTAL_DTO_ID))
                .andExpect(jsonPath("$.carId").value(EXPECTED_CAR_DTO_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @DisplayName("Return rental car. Return RentalDto")
    @Sql(scripts = {
            "classpath:database/user/insert-four-users-into-user-table.sql",
            "classpath:database/user/insert-users_roles-into-users_roles-table.sql",
            "classpath:database/car/insert-cars-into-car-table.sql",
            "classpath:database/rentals/insert-rentals-into-rental-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/delete-all-from-rentals-table.sql",
            "classpath:database/user/delete-four-users-from-users-table.sql",
            "classpath:database/car/delete-three-cars-from-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void returnRental_Valid_ReturnRentalDto() throws Exception {
        User user = getUser();
        mockMvc.perform(
                        post("/rentals/return")
                                .with(user(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(EXPECTED_RENTAL_DTO_ID))
                .andExpect(jsonPath("$.carId").value(EXPECTED_CAR_DTO_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    private User getUser() {
        Role customerRole = new Role();
        customerRole.setName(Role.RoleName.ROLE_CUSTOMER);

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        User user = new User();
        user.setId(USER_ID);
        user.setRoles(roles);
        user.setFirstName("FourthFirstName");
        user.setFirstName("FourthLastName");
        user.setEmail("fourth@email.com");
        return user;
    }

    private CreateRentalRequestDto getCreateRentalRequestDto(Long carId) {
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto();
        requestDto.setRentalDate(LocalDate.now().plusDays(ADD_DAYS_TO_CREATE_RENTAL_DATE));
        requestDto.setReturnDate(LocalDate.now().plusDays(ADD_DAYS_TO_CREATE_RETURN_DATE));
        requestDto.setCarId(carId);
        return requestDto;
    }
}
