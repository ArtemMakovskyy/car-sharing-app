package com.personal.carsharing.carsharingapp.dto.mapper;

import com.personal.carsharing.carsharingapp.config.MapperConfig;
import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.model.Rental;
import com.personal.carsharing.carsharingapp.model.Role;
import com.personal.carsharing.carsharingapp.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentalMapper {

    RentalDto toDto (Rental rental);
    Rental createDtoToEntity(CreateRentalRequestDto requestDto);

}
