package com.kalu.fitnessapp.repository;

import com.kalu.fitnessapp.entity.Goal;
import com.kalu.fitnessapp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class GoalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GoalRepository goalRepository;

    @Test
    void testFindByUser() {
        // Create and persist a sample user with required fields
        User user = new User();
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setPassword("dummyPassword");
        user = entityManager.persistFlushFind(user);

        // Create and persist two goals for this user
        Goal goal1 = new Goal();
        goal1.setGoalType("Lose Weight");
        goal1.setUser(user);
        entityManager.persist(goal1);

        Goal goal2 = new Goal();
        goal2.setGoalType("Build Muscle");
        goal2.setUser(user);
        entityManager.persist(goal2);

        // Create and persist a different user for comparison
        User user2 = new User();
        user2.setUsername("otheruser");
        user2.setFirstname("Other");
        user2.setLastname("User");
        user2.setPassword("dummyPassword");
        user2 = entityManager.persistFlushFind(user2);

        Goal goal3 = new Goal();
        goal3.setGoalType("Improve Stamina");
        goal3.setUser(user2);
        entityManager.persist(goal3);

        // Flush all changes to the database
        entityManager.flush();

        // Fetch goals for the first user using the repository method
        List<Goal> goals = goalRepository.findByUser(user);

        // Verify that exactly 2 goals are returned
        assertNotNull(goals, "Goals list should not be null");
        assertEquals(2, goals.size(), "Should return 2 goals for user 'testuser'");
        assertTrue(goals.stream().anyMatch(g -> "Lose Weight".equals(g.getGoalType())),
                "Expected a goal with type 'Lose Weight'");
        assertTrue(goals.stream().anyMatch(g -> "Build Muscle".equals(g.getGoalType())),
                "Expected a goal with type 'Build Muscle'");
    }

    @Test
    void testDeleteByUser() {
        // Create and persist a sample user with required fields
        User user = new User();
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setPassword("dummyPassword");
        user = entityManager.persistFlushFind(user);

        // Create and persist two goals for this user
        Goal goal1 = new Goal();
        goal1.setGoalType("Lose Weight");
        goal1.setUser(user);
        entityManager.persist(goal1);

        Goal goal2 = new Goal();
        goal2.setGoalType("Build Muscle");
        goal2.setUser(user);
        entityManager.persist(goal2);

        // Flush changes so that the goals are stored in the database
        entityManager.flush();

        // Confirm that the goals exist for the user
        List<Goal> goalsBefore = goalRepository.findByUser(user);
        assertEquals(2, goalsBefore.size(), "There should be 2 goals before deletion");

        // Delete all goals associated with the user
        goalRepository.deleteByUser(user);
        // Flush to force the deletion to occur immediately
        entityManager.flush();

        // Verify that no goals are returned for the user after deletion
        List<Goal> goalsAfter = goalRepository.findByUser(user);
        assertTrue(goalsAfter.isEmpty(), "Goals list should be empty after deletion");
    }
}