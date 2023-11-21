package com.personal.carsharing.carsharingapp.repository.rental;

import com.personal.carsharing.carsharingapp.model.Rental;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("FROM Rental r WHERE r.user.id = :userId AND r.isActive = :isActive")
    List<Rental> findAllByUserIdAndActive(Long userId, Boolean isActive, Pageable pageable);

    List<Rental> findByIsActive(Boolean isActive);

    @Query("""
            FROM Rental r join FETCH r.user u join FETCH r.car c 
            WHERE r.isDeleted = false 
            AND r.isActive = true 
            AND u.telegramChatId IS NOT NULL
             """)
    List<Rental> findAllDetailedRentalsWithTelegramChatId();
}
