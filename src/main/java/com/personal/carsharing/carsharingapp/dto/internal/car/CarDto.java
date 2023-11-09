package com.personal.carsharing.carsharingapp.dto.internal.car;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private String type;
    private Integer inventory;
    private BigDecimal dailyFee;
}
