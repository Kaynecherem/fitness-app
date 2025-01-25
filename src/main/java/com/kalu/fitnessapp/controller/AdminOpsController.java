package com.kalu.fitnessapp.controller;

import com.kalu.fitnessapp.entity.Goal;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.WellBeingLog;
import com.kalu.fitnessapp.service.GoalService;
import com.kalu.fitnessapp.service.UserService;
import com.kalu.fitnessapp.service.WellBeingLogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminOpsController {

    private final UserService userService;
    private final GoalService goalService;
    private final WellBeingLogService wellBeingLogService;

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable page) {
        return ResponseEntity.ok(userService.fetchAllUsers(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getSingleUser(@PathVariable(name = "id") Long userId) {
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSingleUser(@PathVariable(name = "id") Long userId) {
        User user = userService.findUserById(userId);
        String res = userService.deleteUser(user);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}/goals")
    public ResponseEntity<List<Goal>> getSingleUserGoals(@PathVariable(name = "id") Long userId) {
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(goalService.getGoalsByUser(user));
    }

    @GetMapping("/{id}/well-being-logs")
    public ResponseEntity<List<WellBeingLog>> getSingleUserWellBeingLogs(@PathVariable(name = "id") Long userId) {
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(wellBeingLogService.getLogsByUser(user));
    }
}
