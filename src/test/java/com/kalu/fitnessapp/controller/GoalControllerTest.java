// package com.kalu.fitnessapp.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.kalu.fitnessapp.entity.Goal;
// import com.kalu.fitnessapp.entity.User;
// import com.kalu.fitnessapp.service.GoalService;
// import com.kalu.fitnessapp.service.UserService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// import static org.hamcrest.Matchers.is;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @ActiveProfiles("test")
// @WebMvcTest(GoalController.class)
// @Import(GoalControllerTest.TestConfig.class)
// class GoalControllerTest {

//     // --- Test Configuration: manually register mocks ---
//     @TestConfiguration
//     static class TestConfig {
//         @Bean
//         public GoalService goalService() {
//             return mock(GoalService.class);
//         }

//         @Bean
//         public UserService userService() {
//             return mock(UserService.class);
//         }
//     }

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     // Autowired mocks from our TestConfig
//     @Autowired
//     private GoalService goalService;

//     @Autowired
//     private UserService userService;

//     // --- Helper method to create a sample User ---
//     private User createSampleUser() {
//         User user = new User();
//         user.setId(1L);
//         user.setUsername("student1");
//         return user;
//     }

//     // --- Helper method to create a sample Goal ---
//     private Goal createSampleGoal(Long id, User user) {
//         Goal goal = new Goal();
//         goal.setId(id);
//         goal.setGoalType("Lose Weight");
//         goal.setDescription("Lose 5 kg in 2 months");
//         goal.setTargetDate(LocalDate.now().plusMonths(2));
//         goal.setAchieved(false);
//         goal.setUser(user);
//         return goal;
//     }

//     // --- Test for POST /api/goals (create goal) ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testCreateGoal() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         Goal inputGoal = new Goal();
//         // (input goal does not have an id, and user will be set by controller)
//         inputGoal.setGoalType("Lose Weight");
//         inputGoal.setDescription("Lose 5 kg in 2 months");
//         inputGoal.setTargetDate(LocalDate.now().plusMonths(2));
//         inputGoal.setAchieved(false);

//         // The controller will set the user, so create the expected goal
//         Goal createdGoal = createSampleGoal(100L, user);

//         // Stubbing: when the controller looks up the authenticated user by username...
//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         // ...and when a goal is created, return the created goal with an id.
//         when(goalService.createGoal(any(Goal.class))).thenReturn(createdGoal);

//         String inputJson = objectMapper.writeValueAsString(inputGoal);

//         // Act & Assert
//         mockMvc.perform(post("/api/goals")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(inputJson))
//                 .andExpect(status().isCreated())
//                 .andExpect(header().string("Location", is("/api/goals/" + createdGoal.getId())))
//                 .andExpect(jsonPath("$.id", is(createdGoal.getId().intValue())))
//                 .andExpect(jsonPath("$.goalType", is(createdGoal.getGoalType())))
//                 .andExpect(jsonPath("$.description", is(createdGoal.getDescription())));
//     }

//     // --- Test for GET /api/goals (get goals for authenticated user) ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testGetUserGoals() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         Goal goal = createSampleGoal(101L, user);
//         List<Goal> goals = List.of(goal);

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(goalService.getGoalsByUser(user)).thenReturn(goals);

//         // Act & Assert
//         mockMvc.perform(get("/api/goals")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].id", is(goal.getId().intValue())))
//                 .andExpect(jsonPath("$[0].goalType", is(goal.getGoalType())));
//     }

//     // --- Test for PUT /api/goals/{id} (update goal) ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testUpdateGoal() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         Long goalId = 102L;

//         // Existing goal (belongs to authenticated user)
//         Goal existingGoal = createSampleGoal(goalId, user);

//         // Updated values (sent in the request)
//         Goal updateRequest = new Goal();
//         updateRequest.setGoalType("Build Muscle");
//         updateRequest.setDescription("Gain 5 kg of muscle in 3 months");
//         updateRequest.setTargetDate(LocalDate.now().plusMonths(3));
//         updateRequest.setAchieved(true);

//         // Expected goal after update
//         Goal updatedGoal = createSampleGoal(goalId, user);
//         updatedGoal.setGoalType(updateRequest.getGoalType());
//         updatedGoal.setDescription(updateRequest.getDescription());
//         updatedGoal.setTargetDate(updateRequest.getTargetDate());
//         updatedGoal.setAchieved(updateRequest.isAchieved());

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(goalService.getGoalById(goalId)).thenReturn(Optional.of(existingGoal));
//         when(goalService.updateGoal(existingGoal)).thenReturn(updatedGoal);

//         String updateJson = objectMapper.writeValueAsString(updateRequest);

//         // Act & Assert
//         mockMvc.perform(put("/api/goals/{id}", goalId)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(updateJson))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id", is(updatedGoal.getId().intValue())))
//                 .andExpect(jsonPath("$.goalType", is(updatedGoal.getGoalType())))
//                 .andExpect(jsonPath("$.description", is(updatedGoal.getDescription())))
//                 .andExpect(jsonPath("$.achieved", is(updatedGoal.isAchieved())));
//     }

//     // --- Test for DELETE /api/goals/{id} (delete goal) ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testDeleteGoal() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         Long goalId = 103L;

//         Goal goal = createSampleGoal(goalId, user);
//         String expectedMessage = "Goal is removed: " + goalId;

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(goalService.getGoalById(goalId)).thenReturn(Optional.of(goal));
//         when(goalService.deleteGoal(goalId)).thenReturn(expectedMessage);

//         // Act & Assert
//         mockMvc.perform(delete("/api/goals/{id}", goalId)
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(expectedMessage));
//     }
// }
