package com.personal.carsharing.carsharingapp.repository.car;

import com.personal.carsharing.carsharingapp.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}

