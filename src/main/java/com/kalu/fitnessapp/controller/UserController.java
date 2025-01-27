package com.kalu.fitnessapp.controller;

import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.service.AuthService;
import com.kalu.fitnessapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    private static final String USER_ERROR_MESSAGE = "User not found";

    @PostMapping("/auth")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody User user, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authService.authenticateUser(user, httpServletRequest));
    }

    // User registration
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.registerUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/api/users/{id}")
                .buildAndExpand(newUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(newUser);
    }

    // Get authenticated user's details
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_ERROR_MESSAGE));
        return ResponseEntity.ok(user);
    }

    // Update authenticated user's details
    @PutMapping("/me")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    public ResponseEntity<User> updateUser(
            @RequestBody User updateuser,
            Authentication authentication) {
        String username = authentication.getName();
        User existingUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_ERROR_MESSAGE));

        // Update user details (excluding password and roles for simplicity)
        existingUser.setFirstname(updateuser.getFirstname());
        existingUser.setLastname(updateuser.getLastname());

        User savedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(savedUser);
    }

    // Delete authenticated user's account
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    public ResponseEntity<String> deleteUser(Authentication authentication) {

        String username = authentication.getName(); //Email
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_ERROR_MESSAGE));

        String res = userService.deleteUser(user);
        return ResponseEntity.ok(res);
    }
}
