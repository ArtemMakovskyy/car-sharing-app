package com.personal.carsharing.carsharingapp.service.impl;

import com.personal.carsharing.carsharingapp.dto.internal.user.UserRegistrationRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.user.UserResponseDto;
import com.personal.carsharing.carsharingapp.dto.mapper.UserMapper;
import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.exception.RegistrationException;
import com.personal.carsharing.carsharingapp.model.Role;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.repository.role.RoleRepository;
import com.personal.carsharing.carsharingapp.repository.user.UserRepository;
import com.personal.carsharing.carsharingapp.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto updateRole(Long userId, String role) {
        final User userFromDb = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id " + userId));
        final Role roleFromDb = roleRepository.findByName(Role.RoleName.valueOf(role)).orElseThrow(
                () -> new EntityNotFoundException("Can't find role " + role));
        userFromDb.setRoles(Set.of(roleFromDb));
        return userMapper.toDto(userRepository.save(userFromDb));
    }

    @Override
    public UserResponseDto updateInfo(
            Authentication authentication, UserRegistrationRequestDto requestDto) {
        final User userFromAuthentication = (User) authentication.getPrincipal();
        userFromAuthentication.setEmail(requestDto.getEmail());
        userFromAuthentication.setFirstName(requestDto.getFirstName());
        userFromAuthentication.setLastName(requestDto.getLastName());
        userFromAuthentication.setPassword(requestDto.getPassword());
        return userMapper.toDto(userRepository.save(userFromAuthentication));
    }

    @Override
    public UserResponseDto getUserFromAuthentication(Authentication authentication) {
        final User principal = (User) authentication.getPrincipal();
        return userMapper.toDto(principal);
    }

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration.");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        Role roleUser =
                roleRepository.findById(3L).orElseThrow(
                        () -> new RuntimeException("Can't find ROLE_CUSTOMER by id"));
        user.setRoles(Set.of(roleUser));

        final User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
