package com.personal.carsharing.carsharingapp.repository.role;

import com.personal.carsharing.carsharingapp.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(Role.RoleName roleName);
}
