package com.personal.carsharing.carsharingapp.dto.mapper;

import com.personal.carsharing.carsharingapp.config.MapperConfig;
import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.car.CreateCarRequestDto;
import com.personal.carsharing.carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CarMapper {

    CarDto toDto(Car car);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "toUpperCase")
    Car fromCreateDtoToEntity(CreateCarRequestDto requestDto);

    @Named("toUpperCase")
    default String toUpperCase(String value) {
        if (value == null) {
            return null;
        }
        return value.toUpperCase();
    }
}
