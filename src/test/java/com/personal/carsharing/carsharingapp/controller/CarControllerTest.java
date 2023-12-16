package com.personal.carsharing.carsharingapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.car.CreateCarRequestDto;
import java.math.BigDecimal;
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
class CarControllerTest {
    protected static MockMvc mockMvc;
    private static final Long DELETED_CAR_ID = 1L;
    private static final Long UPDATED_CAR_ID = 2L;
    private static final Long GET_CAR_ID = 3L;

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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/car/delete-subaru-outback-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new car with valid data")
    void addCar_ValidRequestDto_Success() throws Exception {
        //given
        CreateCarRequestDto createCarDto = getCreateCarRequestDtoForCreate();
        CarDto expected = getCarDtoForCreate();

        //when, then
        mockMvc.perform(post("/cars")
                        .content(objectMapper.writeValueAsString(createCarDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.model").value(expected.model()))
                .andExpect(jsonPath("$.brand").value(expected.brand()))
                .andExpect(jsonPath("$.type").value(expected.type()))
                .andExpect(jsonPath("$.inventory", is(expected.inventory())))
                .andExpect(jsonPath("$.dailyFee", is(expected.dailyFee().doubleValue())));
    }

    @Test
    @DisplayName("Find all existent cars")
    void getAllCars_WithValidData_Success() throws Exception {
        mockMvc.perform(get("/cars")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "model")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$[0].model", is("3 Series")))
                .andExpect(jsonPath("$[1].model", is("Camry")))
                .andExpect(jsonPath("$[2].model", is("Civic")))
                .andExpect(jsonPath("$[3].model", is("Explorer")))
                .andExpect(jsonPath("$[4].model", is("Focus")))
                .andExpect(jsonPath("$[5].model", is("Logan")))
                .andExpect(jsonPath("$[6].model", is("Logan")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get existent car by valid ID")
    public void getCarById_ValidCarId_ReturnCarDto() throws Exception {
        // Given When and Then
        mockMvc.perform(
                        get("/cars/{id}", GET_CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(GET_CAR_ID))
                .andExpect(jsonPath("$.model").value("Civic"))
                .andExpect(jsonPath("$.brand").value("Honda"))
                .andExpect(jsonPath("$.type").value("HATCHBACK"));
    }

    @Test
    @DisplayName("Update existing car by valid id and new car data, should return carDto")
    @WithMockUser(roles = "ADMIN")
    void updateCarById_ValidData_Success() throws Exception {
        //Given, when and then
        mockMvc.perform(
                        put("/cars/{id}", UPDATED_CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        getCreateCarRequestDtoForUpdate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model", is(getCarDtoForUpdate().model())))
                .andExpect(jsonPath("$.brand", is(getCarDtoForUpdate().brand())))
                .andExpect(jsonPath("$.type", is(getCarDtoForUpdate().type())))
                .andExpect(jsonPath("$.inventory", is(getCarDtoForUpdate().inventory())))
                .andExpect(jsonPath("$.dailyFee",
                        is(getCarDtoForUpdate().dailyFee().doubleValue())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete existent car by valid ID")
    @Sql(scripts = "classpath:database/car/update-soft-deleted-car-into-car-table-to-able.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCar_ValidCarId_Success() throws Exception {
        // Given
        long categoryId = DELETED_CAR_ID;
        // When and Then
        mockMvc.perform(delete("/cars/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private CreateCarRequestDto getCreateCarRequestDtoForUpdate() {
        return new CreateCarRequestDto(
                "Logan",
                "Dacia",
                "UNIVERSAL",
                1,
                new BigDecimal("26.5"));
    }

    private CreateCarRequestDto getCreateCarRequestDtoForCreate() {
        return new CreateCarRequestDto(
                "Subaru",
                "Outback",
                "UNIVERSAL",
                1,
                new BigDecimal("30.5"));
    }

    private CarDto getCarDtoForUpdate() {
        return new CarDto(
                2L,
                "Logan",
                "Dacia",
                "UNIVERSAL",
                1,
                new BigDecimal("26.5"));
    }

    private CarDto getCarDtoForCreate() {
        return new CarDto(
                100L,
                "Subaru",
                "Outback",
                "UNIVERSAL",
                1,
                new BigDecimal("30.5"));
    }
}
