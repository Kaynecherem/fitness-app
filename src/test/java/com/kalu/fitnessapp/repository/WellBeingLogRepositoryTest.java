package com.kalu.fitnessapp.repository;

import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.WellBeingLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class WellBeingLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WellBeingLogRepository logRepository;

    @Test
    void testFindByUser() {
        // Arrange: Create and persist a sample user with required fields
        User user = new User();
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setPassword("dummyPassword");
        user = entityManager.persistFlushFind(user);

        // Create and persist two WellBeingLog records for the same user
        WellBeingLog log1 = new WellBeingLog();
        log1.setUser(user);
        log1.setDate(LocalDate.now().atStartOfDay());
        log1.setMentalHealthStatus("Good");
        log1.setPhysicalHealthStatus("Excellent");
        entityManager.persist(log1);

        WellBeingLog log2 = new WellBeingLog();
        log2.setUser(user);
        log2.setDate(LocalDate.now().minusDays(1).atStartOfDay());
        log2.setMentalHealthStatus("Average");
        log2.setPhysicalHealthStatus("Good");
        entityManager.persist(log2);

        // Create and persist a WellBeingLog for a different user
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setFirstname("Another");
        anotherUser.setLastname("User");
        anotherUser.setPassword("dummyPassword");
        anotherUser = entityManager.persistFlushFind(anotherUser);

        WellBeingLog log3 = new WellBeingLog();
        log3.setUser(anotherUser);
        log3.setDate(LocalDate.now().atStartOfDay());
        log3.setMentalHealthStatus("Poor");
        log3.setPhysicalHealthStatus("Bad");
        entityManager.persist(log3);

        // Flush all changes to the database
        entityManager.flush();

        // Act: Retrieve logs for the original user using the repository method
        List<WellBeingLog> logs = logRepository.findByUser(user);

        // Assert: Only the two logs associated with "testuser" are returned
        assertNotNull(logs, "Logs list should not be null");
        assertEquals(2, logs.size(), "Expected 2 logs for user 'testuser'");
        for (WellBeingLog log : logs) {
            assertEquals(user.getId(), log.getUser().getId(), "Log should belong to the correct user");
        }
    }

    @Test
    void testDeleteByUser() {
        // Arrange: Create and persist a sample user with required fields
        User user = new User();
        user.setUsername("deleteuser");
        user.setFirstname("Delete");
        user.setLastname("User");
        user.setPassword("dummyPassword");
        user = entityManager.persistFlushFind(user);

        // Create and persist two WellBeingLog records for this user
        WellBeingLog log1 = new WellBeingLog();
        log1.setUser(user);
        log1.setDate(LocalDate.now().atStartOfDay());
        log1.setMentalHealthStatus("Good");
        log1.setPhysicalHealthStatus("Good");
        entityManager.persist(log1);

        WellBeingLog log2 = new WellBeingLog();
        log2.setUser(user);
        log2.setDate(LocalDate.now().minusDays(1).atStartOfDay());
        log2.setMentalHealthStatus("Bad");
        log2.setPhysicalHealthStatus("Poor");
        entityManager.persist(log2);

        // Flush changes to ensure the logs are stored in the database
        entityManager.flush();

        // Verify that the logs exist for the user
        List<WellBeingLog> logsBefore = logRepository.findByUser(user);
        assertEquals(2, logsBefore.size(), "There should be 2 logs before deletion");

        // Act: Delete all logs associated with the user
        logRepository.deleteByUser(user);
        entityManager.flush();

        // Assert: After deletion, no logs should be found for the user
        List<WellBeingLog> logsAfter = logRepository.findByUser(user);
        assertTrue(logsAfter.isEmpty(), "Logs list should be empty after deletion");
    }
}