package com.personal.carsharing.carsharingapp.dto.mapper;

import com.personal.carsharing.carsharingapp.config.MapperConfig;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseWithChatIdDto;
import com.personal.carsharing.carsharingapp.model.Role;
import com.personal.carsharing.carsharingapp.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponseDto toDto(User user);

    UserResponseWithChatIdDto toDtoWithChatId(User user);

    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void setUserRoles(@MappingTarget UserResponseDto userDto, User user) {
        userDto.setRoles(mapRoles(user.getRoles()));
    }
}
