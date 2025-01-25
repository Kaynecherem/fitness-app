package com.kalu.fitnessapp.controller;

import com.kalu.fitnessapp.entity.Tip;
import com.kalu.fitnessapp.service.TipService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tips")
public class TipController {

    private final TipService tipService;

    // Create a new tip (Assuming only admin can create tips)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Tip> createTip(@RequestBody Tip tip, Authentication authentication) {
        // Optionally, check if the user has admin privileges
        // For simplicity, assuming all authenticated users can create tips
        Tip newTip = tipService.createTip(tip);
        return ResponseEntity.ok(newTip);
    }

    // Get all tips
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    public ResponseEntity<List<Tip>> getAllTips() {
        List<Tip> tips = tipService.getAllTips();
        return ResponseEntity.ok(tips);
    }

    // Update an existing tip
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Tip> updateTip(
            @PathVariable Long id,
            @RequestBody Tip updateTip) {
        Tip existingTip = tipService.getTipById(id)
                .orElseThrow(() -> new RuntimeException("Tip not found"));

        // Update the tips details
        existingTip.setMessage(updateTip.getMessage());

        Tip savedTip = tipService.updateTip(existingTip);
        return ResponseEntity.ok(savedTip);
    }

    // Delete an existing tip
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> deleteTip(@PathVariable Long id) {

        String res = tipService.deleteTip(id);
        return ResponseEntity.ok(res);
    }
}
