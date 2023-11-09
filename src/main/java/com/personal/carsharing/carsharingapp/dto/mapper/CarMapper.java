package com.personal.carsharing.carsharingapp.dto.mapper;

import com.personal.carsharing.carsharingapp.config.MapperConfig;
import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.car.CreateCarRequestDto;
import com.personal.carsharing.carsharingapp.model.Car;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CarMapper {

    CarDto toDto(Car car);

    Car fromCreateDtoToEntity(CreateCarRequestDto requestDto);
}
