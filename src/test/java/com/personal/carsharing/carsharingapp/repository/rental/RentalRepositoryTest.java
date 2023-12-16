package com.personal.carsharing.carsharingapp.repository.rental;

import com.personal.carsharing.carsharingapp.model.Rental;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalRepositoryTest {
    private static final int EXCEPTED_LIST_SIZE = 1;
    private static final Long ACTIVE_REGISTERED_IN_CHAT_BOT_USER_ID = 5L;
    private static final String EXPECTED_ACTIVE_USER_FIRST_NAME = "ThirdFirstName";
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Find all active rentals with telegram ID")
    @Sql(scripts = {
            "classpath:database/user/insert-four-users-into-user-table.sql",
            "classpath:database/user/insert-users_roles-into-users_roles-table.sql",
            "classpath:database/car/insert-cars-into-car-table.sql",
            "classpath:database/rentals/insert-rentals-into-rental-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/delete-all-from-rentals-table.sql",
            "classpath:database/user/delete-four-users-from-users-table.sql",
            "classpath:database/car/delete-three-cars-from-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllDetailedRentalsWithTelegramChatId_ExistentRentals_ReturnOneRentals() {
        final List<Rental> allDetailedRentalsWithTelegramChatId =
                rentalRepository.findAllDetailedRentalsWithTelegramChatId();
        final String actualFirstName = allDetailedRentalsWithTelegramChatId.stream()
                .findFirst()
                .orElseThrow()
                .getUser().getFirstName();
        Assertions.assertEquals(EXCEPTED_LIST_SIZE, allDetailedRentalsWithTelegramChatId.size());
        Assertions.assertEquals(EXPECTED_ACTIVE_USER_FIRST_NAME, actualFirstName);
    }

    @Test
    @DisplayName("Find all active rentals. Return one rental")
    @Sql(scripts = {
            "classpath:database/user/insert-four-users-into-user-table.sql",
            "classpath:database/user/insert-users_roles-into-users_roles-table.sql",
            "classpath:database/car/insert-cars-into-car-table.sql",
            "classpath:database/rentals/insert-rentals-into-rental-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/delete-all-from-rentals-table.sql",
            "classpath:database/user/delete-four-users-from-users-table.sql",
            "classpath:database/car/delete-three-cars-from-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByUserIdAndActive_ExistentRentals_ReturnOneRentals() {
        final Page<Rental> allByUserIdAndActive =
                rentalRepository.findAllByUserIdAndActive(
                        ACTIVE_REGISTERED_IN_CHAT_BOT_USER_ID,
                        true,
                        PAGE_REQUEST);
        final String actualFirstName = allByUserIdAndActive.getContent().stream()
                .findFirst()
                .orElseThrow()
                .getUser()
                .getFirstName();
        Assertions.assertEquals(EXCEPTED_LIST_SIZE, allByUserIdAndActive.getContent().size());
        Assertions.assertEquals(EXPECTED_ACTIVE_USER_FIRST_NAME,actualFirstName);
    }
}
