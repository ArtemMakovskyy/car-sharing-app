package com.personal.carsharing.carsharingapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    @PostMapping
    public ResponseEntity<?> addRental(
    //            @RequestBody RentalRequest request
    ) {
        // Implement add rental logic and return appropriate response
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getRentalsByUser(
            @RequestParam Long userId, @RequestParam boolean isActive) {
        // Implement get rentals by user ID and active status logic and return rentals
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRentalDetails(@PathVariable Long id) {
        // Implement get rental details logic and return rental information
        return null;
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnRental(@PathVariable Long id
    //            , @RequestBody RentalReturnRequest request
    ) {
        // Implement return rental logic and update car inventory, then return appropriate response
        return null;
    }
}
