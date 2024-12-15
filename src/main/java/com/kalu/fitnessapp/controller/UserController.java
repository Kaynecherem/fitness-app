package com.kalu.fitnessapp.controller;

import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // User registration
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.registerUser(user);
        return ResponseEntity.ok(newUser);
    }

    // Get authenticated user's details
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // Update authenticated user's details
    @PutMapping("/me")
    public ResponseEntity<User> updateUser(
            @RequestBody User updateuser,
            Authentication authentication) {
        String username = authentication.getName();
        User existingUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user details (excluding password and roles for simplicity)
        existingUser.setUsername(updateuser.getUsername());

        User savedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(savedUser);
    }

    // Delete authenticated user's account
    @DeleteMapping("/me")
    public ResponseEntity <Void> deleteUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }
}
