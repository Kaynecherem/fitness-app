// package com.kalu.fitnessapp.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.kalu.fitnessapp.entity.User;
// import com.kalu.fitnessapp.entity.WellBeingLog;
// import com.kalu.fitnessapp.service.UserService;
// import com.kalu.fitnessapp.service.WellBeingLogService;
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
// import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
// @WebMvcTest(WellBeingLogController.class)
// @Import(WellBeingLogControllerTest.TestConfig.class)
// class WellBeingLogControllerTest {

//     // --- Test Configuration: manually register mocks ---
//     @TestConfiguration
//     static class TestConfig {
//         @Bean
//         public WellBeingLogService wellBeingLogService() {
//             return mock(WellBeingLogService.class);
//         }

//         @Bean
//         public UserService userService() {
//             return mock(UserService.class);
//         }
//     }

//     @Autowired
//     private MockMvc mockMvc;

//     // ObjectMapper is used for JSON (de)serialization
//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private WellBeingLogService logService;

//     @Autowired
//     private UserService userService;

//     // --- Helper method to create a sample User ---
//     private User createSampleUser() {
//         User user = new User();
//         user.setId(1L);
//         user.setUsername("student1");
//         // Set additional user fields as needed
//         return user;
//     }

//     // --- Helper method to create a sample WellBeingLog ---
//     private WellBeingLog createSampleLog(Long id, User user) {
//         WellBeingLog log = new WellBeingLog();
//         log.setId(id);
//         log.setDate(LocalDate.now().atStartOfDay());
//         log.setMentalHealthStatus("Good");
//         log.setPhysicalHealthStatus("Average");
//         log.setUser(user);
//         return log;
//     }

//     // --- Test: Create a new log (POST /api/logs) ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testCreateLog() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         WellBeingLog inputLog = new WellBeingLog();
//         inputLog.setDate(LocalDate.now().atStartOfDay());
//         inputLog.setMentalHealthStatus("Good");
//         inputLog.setPhysicalHealthStatus("Average");
//         // Note: the controller sets the user on the log

//         WellBeingLog createdLog = createSampleLog(100L, user);

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(logService.createLog(any(WellBeingLog.class))).thenReturn(createdLog);

//         String inputJson = objectMapper.writeValueAsString(inputLog);

//         // Act & Assert
//         mockMvc.perform(post("/api/logs")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(inputJson))
//                 .andExpect(status().isCreated())
//                 // Verify the Location header points to the new resource URI
//                 .andExpect(header().string("Location",
//                         is(ServletUriComponentsBuilder.fromCurrentRequest()
//                                 .path("/{id}")
//                                 .buildAndExpand(createdLog.getId()).toUri().toString())))
//                 .andExpect(jsonPath("$.id", is(createdLog.getId().intValue())))
//                 .andExpect(jsonPath("$.mentalHealthStatus", is(createdLog.getMentalHealthStatus())))
//                 .andExpect(jsonPath("$.physicalHealthStatus", is(createdLog.getPhysicalHealthStatus())));
//     }

//     // --- Test: Get logs for the authenticated user (GET /api/logs) ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testGetUserLogs() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         WellBeingLog log = createSampleLog(101L, user);
//         List<WellBeingLog> logs = List.of(log);

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(logService.getLogsByUser(user)).thenReturn(logs);

//         // Act & Assert
//         mockMvc.perform(get("/api/logs")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].id", is(log.getId().intValue())))
//                 .andExpect(jsonPath("$[0].mentalHealthStatus", is(log.getMentalHealthStatus())));
//     }

//     // --- Test: Update an existing log (PUT /api/logs/{id}) ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testUpdateLog() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         Long logId = 102L;
//         WellBeingLog existingLog = createSampleLog(logId, user);

//         // Create update payload with new data
//         WellBeingLog updatePayload = new WellBeingLog();
//         updatePayload.setDate(LocalDate.now().plusDays(1).atStartOfDay());
//         updatePayload.setMentalHealthStatus("Excellent");
//         updatePayload.setPhysicalHealthStatus("Good");

//         // Expected updated log after update operation
//         WellBeingLog updatedLog = createSampleLog(logId, user);
//         updatedLog.setDate(updatePayload.getDate());
//         updatedLog.setMentalHealthStatus(updatePayload.getMentalHealthStatus());
//         updatedLog.setPhysicalHealthStatus(updatePayload.getPhysicalHealthStatus());

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(logService.getLogById(logId)).thenReturn(Optional.of(existingLog));
//         when(logService.updateLog(existingLog)).thenReturn(updatedLog);

//         String updateJson = objectMapper.writeValueAsString(updatePayload);

//         // Act & Assert
//         mockMvc.perform(put("/api/logs/{id}", logId)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(updateJson))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id", is(updatedLog.getId().intValue())))
//                 .andExpect(jsonPath("$.mentalHealthStatus", is(updatedLog.getMentalHealthStatus())))
//                 .andExpect(jsonPath("$.physicalHealthStatus", is(updatedLog.getPhysicalHealthStatus())));
//     }

//     // --- Test: Update log - Unauthorized when the log does not belong to the authenticated user ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testUpdateLog_Unauthorized() throws Exception {
//         // Arrange
//         // Authenticated user "student1"
//         User user = createSampleUser();
//         // A log that belongs to another user
//         User anotherUser = new User();
//         anotherUser.setId(2L);
//         anotherUser.setUsername("otherUser");

//         Long logId = 103L;
//         WellBeingLog existingLog = createSampleLog(logId, anotherUser);

//         // Update payload (data does not matter as update should be blocked)
//         WellBeingLog updatePayload = new WellBeingLog();
//         updatePayload.setDate(LocalDate.now().plusDays(1).atStartOfDay());
//         updatePayload.setMentalHealthStatus("Excellent");
//         updatePayload.setPhysicalHealthStatus("Good");

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(logService.getLogById(logId)).thenReturn(Optional.of(existingLog));

//         String updateJson = objectMapper.writeValueAsString(updatePayload);

//         // Act & Assert: Expect 403 Forbidden because the log does not belong to the authenticated user.
//         mockMvc.perform(put("/api/logs/{id}", logId)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(updateJson))
//                 .andExpect(status().isForbidden());
//     }

//     // --- Test: Delete an existing log (DELETE /api/logs/{id}) ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     public void testDeleteLog() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         Long logId = 104L;
//         WellBeingLog log = createSampleLog(logId, user);

//         String expectedMessage = "Wellbeing Log is removed: " + logId;

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(logService.getLogById(logId)).thenReturn(Optional.of(log));
//         when(logService.deleteLog(logId)).thenReturn(expectedMessage);

//         // Act & Assert
//         mockMvc.perform(delete("/api/logs/{id}", logId)
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(expectedMessage));
//     }

//     // --- Test: Delete log - Unauthorized when the log does not belong to the authenticated user ---
//     @Test
//     @WithMockUser(username = "student1", authorities = "STUDENT")
//     void testDeleteLog_Unauthorized() throws Exception {
//         // Arrange
//         User user = createSampleUser();
//         // A log that belongs to another user
//         User anotherUser = new User();
//         anotherUser.setId(2L);
//         anotherUser.setUsername("otherUser");

//         Long logId = 105L;
//         WellBeingLog log = createSampleLog(logId, anotherUser);

//         when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
//         when(logService.getLogById(logId)).thenReturn(Optional.of(log));

//         // Act & Assert: Expect 403 Forbidden since the log does not belong to the authenticated user.
//         mockMvc.perform(delete("/api/logs/{id}", logId)
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isForbidden());
//     }
// }
