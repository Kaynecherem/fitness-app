package com.kalu.fitnessapp.repository;

import com.kalu.fitnessapp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        // Arrange: Create and persist a user with required fields
        User user = new User();
        user.setUsername("john_doe");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setPassword("secret123");
        // Persist the user to the in-memory database
        entityManager.persistAndFlush(user);

        // Act: Retrieve the user using the repository method
        Optional<User> foundUser = userRepository.findByUsername("john_doe");

        // Assert: The user should be found and have the expected username
        assertTrue(foundUser.isPresent(), "User should be found by username");
        assertEquals("john_doe", foundUser.get().getUsername(), "The username should match");
    }

    @Test
    void testExistsByUsername() {
        // Arrange: Create and persist a user with required fields
        User user = new User();
        user.setUsername("jane_doe");
        user.setFirstname("Jane");
        user.setLastname("Doe");
        user.setPassword("password456");
        entityManager.persistAndFlush(user);

        // Act: Verify that the repository reports the user exists
        boolean exists = userRepository.existsByUsername("jane_doe");
        // Also check a username that was not persisted
        boolean notExists = userRepository.existsByUsername("non_existing_user");

        // Assert:
        assertTrue(exists, "User with username 'jane_doe' should exist");
        assertFalse(notExists, "User with username 'non_existing_user' should not exist");
    }
}