package com.personal.carsharing.carsharingapp.repository.user;

import com.personal.carsharing.carsharingapp.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    private static final String FIRST_EMAIL = "first@email.com";
    private static final String SECOND_EMAIL = "second@email.com";
    private static final String THIRD_EMAIL = "third@email.com";
    private static final String USER_NAME_OF_FIRST_EMAIL = "FirstName";
    private static final String USER_NAME_OF_SECOND_EMAIL = "SecondFirstName";
    private static final String USER_NAME_OF_THIRD_EMAIL = "ThirdFirstName";

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by valid email. Return existing user.")
    @Sql(scripts = {
            "classpath:database/user/insert-four-users-into-user-table.sql",
            "classpath:database/user/insert-users_roles-into-users_roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/user/delete-four-users-from-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findUserByEmail_ExistentEmail_ReturnUser() {

        final User firstUser = userRepository.findUserByEmail(FIRST_EMAIL).orElseThrow();
        final User secondUser = userRepository.findUserByEmail(SECOND_EMAIL).orElseThrow();
        final User thirdUser = userRepository.findUserByEmail(THIRD_EMAIL).orElseThrow();

        Assertions.assertEquals(firstUser.getFirstName(), USER_NAME_OF_FIRST_EMAIL);
        Assertions.assertEquals(secondUser.getFirstName(), USER_NAME_OF_SECOND_EMAIL);
        Assertions.assertEquals(thirdUser.getFirstName(), USER_NAME_OF_THIRD_EMAIL);
    }
}
