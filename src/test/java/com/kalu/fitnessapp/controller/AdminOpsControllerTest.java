package com.kalu.fitnessapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalu.fitnessapp.entity.Goal;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.WellBeingLog;
import com.kalu.fitnessapp.service.GoalService;
import com.kalu.fitnessapp.service.UserService;
import com.kalu.fitnessapp.service.WellBeingLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AdminOpsController.class)
@Import(AdminOpsControllerTest.TestConfig.class)
class AdminOpsControllerTest {

    // Define a static configuration class that registers our mocks as beans
    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }

        @Bean
        public GoalService goalService() {
            return mock(GoalService.class);
        }

        @Bean
        public WellBeingLogService wellBeingLogService() {
            return mock(WellBeingLogService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper to convert objects to/from JSON if needed
    @Autowired
    private ObjectMapper objectMapper;

    // Autowire the mocked services so we can define stubbing for them
    @Autowired
    private UserService userService;

    @Autowired
    private GoalService goalService;

    @Autowired
    private WellBeingLogService wellBeingLogService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllUsers() throws Exception {
        // Arrange: Create a sample user and wrap it in a Page implementation
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userService.fetchAllUsers(any(Pageable.class))).thenReturn(userPage);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check that the JSON response contains our sample user
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].username", is("user1")));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetSingleUser() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        when(userService.findUserById(1L)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user1")));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteSingleUser() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        String expectedResponse = "User is removed: 1";

        when(userService.findUserById(1L)).thenReturn(user);
        when(userService.deleteUser(user)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetSingleUserGoals() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Goal goal = new Goal();
        goal.setId(10L);
        // (Optionally set other properties on goal as needed.)

        List<Goal> goals = List.of(goal);
        when(userService.findUserById(1L)).thenReturn(user);
        when(goalService.getGoalsByUser(user)).thenReturn(goals);

        // Act & Assert
        mockMvc.perform(get("/api/users/1/goals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(10)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetSingleUserWellBeingLogs() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        WellBeingLog log = new WellBeingLog();
        log.setId(20L);
        // (Optionally set other properties on the log as needed.)

        List<WellBeingLog> logs = List.of(log);
        when(userService.findUserById(1L)).thenReturn(user);
        when(wellBeingLogService.getLogsByUser(user)).thenReturn(logs);

        // Act & Assert
        mockMvc.perform(get("/api/users/1/well-being-logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(20)));
    }
}