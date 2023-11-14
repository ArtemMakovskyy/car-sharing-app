package com.personal.carsharing.carsharingapp.repository.user;

import com.personal.carsharing.carsharingapp.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User u LEFT JOIN FETCH u.roles r WHERE u.email = :email AND u.isDeleted = FALSE "
            + "AND r.isDeleted = FALSE")
    Optional<User> findUserByEmail(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(Long id);
}
