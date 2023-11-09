package com.personal.carsharing.carsharingapp.dto.internal.car;

import java.math.BigDecimal;
import java.util.Arrays;

import com.personal.carsharing.carsharingapp.model.Car;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCarRequestDto {
    @NotBlank(message = "Model should not be blank")
    private String model;
    @NotBlank(message = "Brand should not be blank")
    private String brand;
    @NotBlank(message = "Type should not be blank")
    private String type;
    @Min(value = 0, message = "Invalid inventory, it cannot be less than zero")
    private Integer inventory;
    @Min(value = 0, message = "Invalid dailyFee, it cannot be less than zero")
    private BigDecimal dailyFee;

    public void setType(String type) {
        this.type = typeValidation(type.toUpperCase());
    }

    private String typeValidation(String type) {
        final Car.Type[] values = Car.Type.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].name().equals(type)) {
                return type.toUpperCase();
            }
        }
        throw new IllegalArgumentException("Invalid car type: " + this.type);
    }
}
