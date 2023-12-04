package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
@Tag(name = "Rental management", description = "Endpoints for managing rentals")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @Operation(summary = "Add new rental",
            description = "Add new car rental and decrease car inventory by 1")
    public RentalDto addRental(
            @RequestBody @Valid CreateRentalRequestDto requestDto,
            Authentication authentication) {
        return rentalService.add(requestDto, authentication);
    }

    @GetMapping("/")
    public List<RentalDto> getRentalsByUserIdAndRentalStatus(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") Boolean isActive,
            Pageable pageable,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            userId = user.getId();
            rentalService.findAllByUserIdAndStatus(userId, isActive, pageable);
        }
        return rentalService.findAllByUserIdAndStatus(userId, isActive, pageable);
    }

    @GetMapping
    public RentalDto getUserRentalDetails(Authentication authentication) {
        return rentalService.getUserRentalDetailsByAuthentication(authentication);
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @PostMapping("/return")
    public RentalDto returnRental(Authentication authentication) {
        return rentalService.returnRentalCar(authentication);
    }
}
