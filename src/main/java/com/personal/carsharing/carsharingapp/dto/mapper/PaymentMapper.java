package com.personal.carsharing.carsharingapp.dto.mapper;

import com.personal.carsharing.carsharingapp.config.MapperConfig;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;
import com.personal.carsharing.carsharingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "rentalId", source = "payment.rental.id")
    PaymentResponseDto toDto(Payment payment);
}
