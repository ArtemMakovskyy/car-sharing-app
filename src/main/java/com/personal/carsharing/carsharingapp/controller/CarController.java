package com.personal.carsharing.carsharingapp.controller;

import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.car.CreateCarRequestDto;
import com.personal.carsharing.carsharingapp.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints to managing cars")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Creating a new Car.",
            description = "Creating a new Car with valid data")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto addCar(
            @RequestBody @Valid CreateCarRequestDto request) {
        return carService.save(request);
    }

    @Operation(summary = "Getting available cars.",
            description = """
                    Retrieve page with all available cars.
                    By default it is first page with 10 cars, sorted ASC by model
                    except those deleted using soft delete.
                    Can use unregistered users""")
    @GetMapping
    public List<CarDto> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "model") String[] sort) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return carService.findAll(pageable);
    }

    @Operation(summary = "Getting available car by id.",
            description = """
                    Retrieve available car by id,
                    if it has not been deleted with soft delete.""")
    @GetMapping("/{id}")
    public CarDto getCarById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "Updating car by id",
            description = "Updating available car by id, "
                    + "except those deleted using soft delete.")
    @PutMapping("/{id}")
    public CarDto updateCarById(
            @PathVariable Long id,
            @RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.update(id, requestDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Deleting car by id",
            description = "Soft deleting available car by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
