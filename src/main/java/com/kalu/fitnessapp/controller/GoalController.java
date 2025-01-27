package com.kalu.fitnessapp.controller;

import com.kalu.fitnessapp.entity.Goal;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.service.GoalService;
import com.kalu.fitnessapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@PreAuthorize("hasAnyAuthority('STUDENT')")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    private User getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Create a new goal
    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        goal.setUser(user);
        Goal newGoal = goalService.createGoal(goal);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/api/goals/{id}")
                .buildAndExpand(newGoal.getId())
                .toUri();

        return ResponseEntity.created(location).body(newGoal);
    }

    // Get goals for the authenticated user
    @GetMapping
    public ResponseEntity<List<Goal>> getUserGoals(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<Goal> goals = goalService.getGoalsByUser(user);
        return ResponseEntity.ok(goals);
    }

    // Update an existing goal
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(
            @PathVariable Long id,
            @RequestBody Goal updateGoal,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        Goal existingGoal = goalService.getGoalById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        // Check if the goal belongs to the authenticated user
        if (!existingGoal.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();// Forbidden
        }

        // Update the goal details
        existingGoal.setGoalType(updateGoal.getGoalType());
        existingGoal.setDescription(updateGoal.getDescription());
        existingGoal.setTargetDate(updateGoal.getTargetDate());
        existingGoal.setAchieved(updateGoal.isAchieved());

        Goal savedGoal = goalService.updateGoal(existingGoal);
        return ResponseEntity.ok(savedGoal);
    }

    // Delete an existing goal
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGoal(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Goal goal = goalService.getGoalById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        // Check if the goal belongs to the authenticated user
        if(!goal.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        String res = goalService.deleteGoal(id);
        return ResponseEntity.ok(res);
    }
}

