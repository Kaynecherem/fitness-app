package com.kalu.fitnessapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.service.AuthService;
import com.kalu.fitnessapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import(UserControllerTest.TestConfig.class)
public class UserControllerTest {

    // --- Test Configuration: manually register mocks ---
    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return mock(AuthService.class);
        }

        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Autowired mocks from our TestConfig
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // --- Test for POST /api/users/auth ---
    @Test
    void testAuthenticateUser() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        Map<String, String> authResponse = Map.of("token", "dummy-token");

        // Stub the authentication service to return our dummy token map.
        when(authService.authenticateUser(any(User.class), any(HttpServletRequest.class)))
                .thenReturn(authResponse);

        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/api/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("dummy-token")));
    }

    // --- Test for POST /api/users/register ---
    @Test
    void testRegisterUser() throws Exception {
        User inputUser = new User();
        inputUser.setUsername("newUser");
        inputUser.setPassword("password");
        // Set other fields as needed

        User registeredUser = new User();
        registeredUser.setId(1L);
        registeredUser.setUsername("newUser");
        // Optionally set other fields

        when(userService.registerUser(any(User.class))).thenReturn(registeredUser);

        String inputJson = objectMapper.writeValueAsString(inputUser);
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isCreated())
                // Expect that the Location header points to /api/users/{id}
                .andExpect(header().string("Location", "/api/users/" + registeredUser.getId()))
                .andExpect(jsonPath("$.id", is(registeredUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(registeredUser.getUsername())));
    }

    // --- Test for GET /api/users/me ---
    @Test
    @WithMockUser(username = "testUser", authorities = {"STUDENT", "ADMIN"})
    void testGetCurrentUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        // Set additional fields if needed

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    // --- Test for PUT /api/users/me ---
    @Test
    @WithMockUser(username = "testUser", authorities = {"STUDENT", "ADMIN"})
    void testUpdateCurrentUser() throws Exception {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testUser");
        existingUser.setFirstname("OldFirst");
        existingUser.setLastname("OldLast");

        // The update payload: only first and last name are updated
        User updateUser = new User();
        updateUser.setFirstname("NewFirst");
        updateUser.setLastname("NewLast");

        // The updated user returned by the service
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("testUser");
        updatedUser.setFirstname("NewFirst");
        updatedUser.setLastname("NewLast");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(existingUser));
        when(userService.updateUser(existingUser)).thenReturn(updatedUser);

        String updateJson = objectMapper.writeValueAsString(updateUser);
        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId().intValue())))
                .andExpect(jsonPath("$.firstname", is(updatedUser.getFirstname())))
                .andExpect(jsonPath("$.lastname", is(updatedUser.getLastname())));
    }

    // --- Test for DELETE /api/users/me ---
    @Test
    @WithMockUser(username = "testUser", authorities = {"STUDENT", "ADMIN"})
    void testDeleteCurrentUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        String deleteMessage = "User is removed: " + user.getId();

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(userService.deleteUser(user)).thenReturn(deleteMessage);

        mockMvc.perform(delete("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(deleteMessage));
    }
}