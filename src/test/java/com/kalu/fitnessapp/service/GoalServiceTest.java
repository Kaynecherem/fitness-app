// package com.kalu.fitnessapp.service;

// import com.kalu.fitnessapp.UserDeletedEvent;
// import com.kalu.fitnessapp.entity.Goal;
// import com.kalu.fitnessapp.entity.User;
// import com.kalu.fitnessapp.repository.GoalRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.test.context.ActiveProfiles;

// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// @ActiveProfiles("test")
// class GoalServiceTest {

//     @Mock
//     private GoalRepository goalRepository;

//     @InjectMocks
//     private GoalService goalService;

//     @BeforeEach
//     void setUp() {
//         // Initialize mocks before each test
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     void testCreateGoal() {
//         // Arrange
//         Goal goal = new Goal();
//         goal.setId(1L); // adjust properties as needed
//         when(goalRepository.save(goal)).thenReturn(goal);

//         // Act
//         Goal createdGoal = goalService.createGoal(goal);

//         // Assert
//         assertNotNull(createdGoal, "The created goal should not be null");
//         assertEquals(1L, createdGoal.getId(), "The goal ID should match the expected value");
//         verify(goalRepository, times(1)).save(goal);
//     }

//     @Test
//     void testGetGoalsByUser() {
//         // Arrange
//         User user = new User();
//         user.setId(1L); // adjust properties as needed

//         Goal goal1 = new Goal();
//         goal1.setId(1L);

//         Goal goal2 = new Goal();
//         goal2.setId(2L);

//         List<Goal> expectedGoals = Arrays.asList(goal1, goal2);
//         when(goalRepository.findByUser(user)).thenReturn(expectedGoals);

//         // Act
//         List<Goal> goals = goalService.getGoalsByUser(user);

//         // Assert
//         assertNotNull(goals, "Returned goals list should not be null");
//         assertEquals(2, goals.size(), "There should be exactly 2 goals returned");
//         verify(goalRepository, times(1)).findByUser(user);
//     }

//     @Test
//     void testGetGoalById_Found() {
//         // Arrange
//         Goal goal = new Goal();
//         goal.setId(1L);
//         when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

//         // Act
//         Optional<Goal> retrievedGoal = goalService.getGoalById(1L);

//         // Assert
//         assertTrue(retrievedGoal.isPresent(), "Goal should be found");
//         assertEquals(1L, retrievedGoal.get().getId(), "The goal ID should be 1");
//         verify(goalRepository, times(1)).findById(1L);
//     }

//     @Test
//     void testGetGoalById_NotFound() {
//         // Arrange
//         when(goalRepository.findById(1L)).thenReturn(Optional.empty());

//         // Act
//         Optional<Goal> retrievedGoal = goalService.getGoalById(1L);

//         // Assert
//         assertFalse(retrievedGoal.isPresent(), "No goal should be found");
//         verify(goalRepository, times(1)).findById(1L);
//     }

//     @Test
//     void testUpdateGoal() {
//         // Arrange
//         Goal goal = new Goal();
//         goal.setId(1L);
//         when(goalRepository.save(goal)).thenReturn(goal);

//         // Act
//         Goal updatedGoal = goalService.updateGoal(goal);

//         // Assert
//         assertNotNull(updatedGoal, "Updated goal should not be null");
//         verify(goalRepository, times(1)).save(goal);
//     }

//     @Test
//     void testDeleteGoal() {
//         // Arrange
//         Long goalId = 1L;
//         doNothing().when(goalRepository).deleteById(goalId);

//         // Act
//         String response = goalService.deleteGoal(goalId);

//         // Assert
//         assertEquals("Goal is removed: " + goalId, response, "Response should confirm deletion");
//         verify(goalRepository, times(1)).deleteById(goalId);
//     }

//     @Test
//     void testUserDeletionListener() {
//         // Arrange
//         User user = new User();
//         user.setId(1L);
//         UserDeletedEvent event = new UserDeletedEvent(user);
//         doNothing().when(goalRepository).deleteByUser(user);

//         // Act
//         goalService.userDeletionListener(event);

//         // Assert
//         verify(goalRepository, times(1)).deleteByUser(user);
//     }
// }
