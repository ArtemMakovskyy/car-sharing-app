package com.personal.carsharing.carsharingapp.dto.mapper;

import com.personal.carsharing.carsharingapp.config.MapperConfig;
import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDetailedResponseDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentalMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "carId", source = "car.id")
    RentalDto toDto(Rental rental);

    Rental createDtoToEntity(CreateRentalRequestDto requestDto);

    @Mapping(target = "userId", source = "rental.user.id")
    @Mapping(target = "userFirstName", source = "rental.user.firstName")
    RentalDetailedResponseDto toDetailedDto(Rental rental);
}
