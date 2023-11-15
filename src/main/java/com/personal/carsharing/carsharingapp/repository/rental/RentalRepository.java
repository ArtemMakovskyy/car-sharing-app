package com.personal.carsharing.carsharingapp.repository.rental;

import com.personal.carsharing.carsharingapp.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository
        extends JpaRepository<Rental,Long> {
}
