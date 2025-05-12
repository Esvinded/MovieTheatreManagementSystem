package com.CS3332Group5.MovieTheatreManagementSystem.features.user.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.user.dto.CustomerProfileDTO;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.service.CustomerService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Get current customer's profile
    @GetMapping("/profile")
    public ResponseEntity<?> getCustomerProfile(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String username = principal.getName();
        // Fetch and return the profile
        CustomerProfileDTO profile = customerService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    // Update current customer's profile (excluding image)
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(
            Principal principal,
            @Valid @RequestBody CustomerProfileDTO updatedProfile
    ) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        customerService.updateProfile(principal.getName(), updatedProfile);
        return ResponseEntity.ok("Profile updated successfully.");
    }

    @PutMapping("/profile/picture")
    public ResponseEntity<String> updateProfilePicture(
            Principal principal,
            @RequestParam("profileImageUrl") String profileImageUrl
    ) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
    
        customerService.updateProfilePicture(principal.getName(), profileImageUrl);
        return ResponseEntity.ok("Profile picture updated successfully.");
    }
}
