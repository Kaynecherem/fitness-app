 package com.kalu.fitnessapp.service;

 import com.kalu.fitnessapp.UserDeletedEvent;
 import com.kalu.fitnessapp.entity.User;
 import com.kalu.fitnessapp.entity.WellBeingLog;
 import com.kalu.fitnessapp.repository.WellBeingLogRepository;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.*;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.springframework.test.context.ActiveProfiles;

 import java.util.Arrays;
 import java.util.List;
 import java.util.Optional;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.Mockito.*;

 @ActiveProfiles("test")
 @ExtendWith(MockitoExtension.class)
 class WellBeingLogServiceTest {

     @Mock
     private WellBeingLogRepository logRepository;

     @InjectMocks
     private WellBeingLogService wellBeingLogService;

     private User sampleUser;
     private WellBeingLog sampleLog;

     @BeforeEach
     void setUp() {
         // Initialize a sample User (adjust properties as needed)
         sampleUser = new User();
         sampleUser.setId(1L);
         // For example, you might set username or other fields here

         // Initialize a sample WellBeingLog and assign the user
         sampleLog = new WellBeingLog();
         sampleLog.setId(1L);
         sampleLog.setUser(sampleUser);
         // Set other properties of the log as required by your entity
     }

     @Test
     void testCreateLog() {
         // Arrange
         when(logRepository.save(sampleLog)).thenReturn(sampleLog);

         // Act
         WellBeingLog createdLog = wellBeingLogService.createLog(sampleLog);

         // Assert
         assertNotNull(createdLog, "Created log should not be null");
         assertEquals(sampleLog.getId(), createdLog.getId(), "Log IDs should match");
         verify(logRepository, times(1)).save(sampleLog);
     }

     @Test
     void testGetLogsByUser() {
         // Arrange
         List<WellBeingLog> expectedLogs = Arrays.asList(sampleLog);
         when(logRepository.findByUser(sampleUser)).thenReturn(expectedLogs);

         // Act
         List<WellBeingLog> logs = wellBeingLogService.getLogsByUser(sampleUser);

         // Assert
         assertNotNull(logs, "Logs list should not be null");
         assertEquals(1, logs.size(), "Logs list should contain exactly one log");
         verify(logRepository, times(1)).findByUser(sampleUser);
     }

     @Test
     void testGetLogById_Found() {
         // Arrange
         when(logRepository.findById(1L)).thenReturn(Optional.of(sampleLog));

         // Act
         Optional<WellBeingLog> retrievedLog = wellBeingLogService.getLogById(1L);

         // Assert
         assertTrue(retrievedLog.isPresent(), "Expected log to be present");
         assertEquals(sampleLog.getId(), retrievedLog.get().getId(), "Log IDs should match");
         verify(logRepository, times(1)).findById(1L);
     }

     @Test
     void testGetLogById_NotFound() {
         // Arrange
         when(logRepository.findById(2L)).thenReturn(Optional.empty());

         // Act
         Optional<WellBeingLog> retrievedLog = wellBeingLogService.getLogById(2L);

         // Assert
         assertFalse(retrievedLog.isPresent(), "Expected no log to be found");
         verify(logRepository, times(1)).findById(2L);
     }

     @Test
     void testUpdateLog() {
         // Arrange
         when(logRepository.save(sampleLog)).thenReturn(sampleLog);

         // Act
         WellBeingLog updatedLog = wellBeingLogService.updateLog(sampleLog);

         // Assert
         assertNotNull(updatedLog, "Updated log should not be null");
         assertEquals(sampleLog.getId(), updatedLog.getId(), "Log IDs should match");
         verify(logRepository, times(1)).save(sampleLog);
     }

     @Test
     void testDeleteLog() {
         // Arrange
         Long logId = 1L;
         doNothing().when(logRepository).deleteById(logId);

         // Act
         String response = wellBeingLogService.deleteLog(logId);

         // Assert
         assertEquals("Wellbeing Log is removed: " + logId, response, "Deletion response message should match");
         verify(logRepository, times(1)).deleteById(logId);
     }

     @Test
     void testUserDeletionListener() {
         // Arrange
         // Create an event with the sample user. Assumes UserDeletedEvent has a constructor accepting a User.
         UserDeletedEvent event = new UserDeletedEvent(sampleUser);
         doNothing().when(logRepository).deleteByUser(sampleUser);

         // Act
         wellBeingLogService.userDeletionListener(event);

         // Assert
         verify(logRepository, times(1)).deleteByUser(sampleUser);
     }
 }
