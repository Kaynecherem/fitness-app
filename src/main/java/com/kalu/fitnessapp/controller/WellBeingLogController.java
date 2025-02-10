package com.kalu.fitnessapp.controller;

import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.WellBeingLog;
import com.kalu.fitnessapp.service.UserService;
import com.kalu.fitnessapp.service.WellBeingLogService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/logs")
@PreAuthorize("hasAnyAuthority('STUDENT')")
public class WellBeingLogController {

    private final WellBeingLogService logService;
    private final UserService userService;

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
        log.setDate(LocalDateTime.now());
        WellBeingLog newLog = logService.createLog(log);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newLog.getId())
                .toUri();

        return ResponseEntity.created(location).body(newLog);
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
    public ResponseEntity<String> deleteLog(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        WellBeingLog log = logService.getLogById(id)
                .orElseThrow(() -> new RuntimeException(LOG_NOT_FOUND));

        // Optionally, check if the log belongs to the user
        if (!log.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        String res = logService.deleteLog(id);
        return ResponseEntity.ok(res);
    }
}
