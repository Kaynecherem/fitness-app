package com.kalu.fitnessapp.controller;

import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.WellBeingLog;
import com.kalu.fitnessapp.service.UserService;
import com.kalu.fitnessapp.service.WellBeingLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class WellBeingLogController {

    @Autowired
    private WellBeingLogService logService;

    @Autowired
    private UserService userService;

    private static final String LOG_NOT_FOUND = "Log not found";

    private User getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Create a new log
    @PostMapping
    public ResponseEntity<WellBeingLog> createLog(@RequestBody WellBeingLog log, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        log.setUser(user);
        WellBeingLog newLog = logService.createLog(log);
        return ResponseEntity.ok(newLog);
    }

    // Get logs for the authenticated user
    @GetMapping
    public ResponseEntity<List<WellBeingLog>> getUserLogs(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<WellBeingLog> logs = logService.getLogsByUser(user);
        return ResponseEntity.ok(logs);
    }

    // Update an existing log
    @PutMapping("/{id}")
    public ResponseEntity<WellBeingLog> updateLog(
            @PathVariable Long id,
            @RequestBody WellBeingLog updatedLog,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        WellBeingLog existingLog = logService.getLogById(id)
                .orElseThrow(() -> new RuntimeException(LOG_NOT_FOUND));

        if (!existingLog.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        existingLog.setDate(updatedLog.getDate());
        existingLog.setMentalHealthStatus(updatedLog.getMentalHealthStatus());
        existingLog.setPhysicalHealthStatus(updatedLog.getPhysicalHealthStatus());

        WellBeingLog savedLog = logService.updateLog(existingLog);
        return ResponseEntity.ok(savedLog);
    }

    //Delete an existing log
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        WellBeingLog log = logService.getLogById(id)
                .orElseThrow(() -> new RuntimeException(LOG_NOT_FOUND));

        // Optionally, check if the log belongs to the user
        if (!log.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        logService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
