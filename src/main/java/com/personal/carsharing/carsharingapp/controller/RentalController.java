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

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "Add new rental",
            description = "Add new car rental and decrease car inventory by 1")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    public RentalDto addRental(
            @RequestBody @Valid CreateRentalRequestDto requestDto,
            Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return rentalService.add(requestDto, user);
    }

    @Operation(summary = "Get the rentals by user id and status",
            description = """
                    Retrieve rentals by user id and active status.
                    If user is a CUSTOMER he can get only own data, 
                    if an ADMIN OR MANAGER can get any data.""")
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

    @Operation(summary = "Get information about rental",
            description = "CUSTOMER can get information about his own rental")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping
    public RentalDto getUserRentalDetails(Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return rentalService.getUserRentalDetailsByAuthentication(user.getId());
    }

    @Operation(summary = "Return rental car by CUSTOMER",
            description = "Return rental car by CUSTOMER and decrease car inventory by 1")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @PostMapping("/return")
    public RentalDto returnRental(Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return rentalService.returnRentalCar(user.getId());
    }
}
