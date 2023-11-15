package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.service.RentalService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public RentalDto addRental(
            @RequestBody @Valid CreateRentalRequestDto requestDto,
            Authentication authentication) {
        final User credentials = (User) authentication.getCredentials();
        final User principal = (User) authentication.getPrincipal();
        System.out.println(principal);
        System.out.println(authentication.getName());
        rentalService.add(requestDto,authentication);
        return null;
    }

    @GetMapping("/")
//    GET: /rentals/?user_id=1&is_active=true
    public List<RentalDto> getRentals(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "is_active",  required = false) Boolean isActive
//            @RequestParam(name = "is_active", defaultValue = "true", required = false) Boolean isActive
    ) {
        System.out.println(userId + " " + isActive);
        // Implement get rentals by user ID and active status logic and return rentals
        return null;
    }

    @GetMapping("/{id}")
    public RentalDto getRentalDetails(@PathVariable Long id) {
        // Implement get rental details logic and return rental information
        return null;
    }

    @PostMapping("/return")
    public RentalDto returnRental(@PathVariable Long id
    //            , @RequestBody RentalReturnRequest request
    ) {
        // Implement return rental logic and update car inventory, then return appropriate response
        return null;
    }
}
