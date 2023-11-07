package com.personal.carsharing.carsharingapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {
    @PostMapping
    public ResponseEntity<?> addCar(
    //            @RequestBody CarRequest request
    ) {
        // Implement add car logic and return appropriate response
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getAllCars() {
        // Implement get all cars logic and return list of cars
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarDetails(@PathVariable Long id) {
        // Implement get car details logic and return car information
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCar(@PathVariable Long id
    //            ,
    //                                       @RequestBody CarUpdateRequest request
    ) {
        // Implement update car logic and return appropriate response
        return null;

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        // Implement delete car logic and return appropriate response
        return null;
    }
}
