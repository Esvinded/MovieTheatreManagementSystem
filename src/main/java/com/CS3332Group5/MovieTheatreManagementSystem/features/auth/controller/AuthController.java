package com.CS3332Group5.MovieTheatreManagementSystem.features.auth.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.dto.LoginRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Staff;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Customer Registration
    @PostMapping("/customer/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) {
        // Validate customer fields
        Map<String, String> errors = validateCustomer(customer);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            userService.registerCustomer(customer);
            return ResponseEntity.ok("Customer registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // Customer Login
    @PostMapping("/customer/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest loginRequest) {
        // Validate login fields
        Map<String, String> errors = validateLoginRequest(loginRequest);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            String message = userService.loginCustomer(loginRequest);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // Staff Registration
    @PostMapping("/staff/register")
    public ResponseEntity<?> registerStaff(@RequestBody Staff staff) {
        // Validate staff fields
        Map<String, String> errors = validateStaff(staff);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            userService.registerStaff(staff);
            return ResponseEntity.ok("Staff registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // Staff Login
    @PostMapping("/staff/login")
    public ResponseEntity<?> loginStaff(@RequestBody LoginRequest loginRequest) {
        // Validate login fields
        Map<String, String> errors = validateLoginRequest(loginRequest);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            String message = userService.loginStaff(loginRequest);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // Validation for Customer and Staff Registration
    private Map<String, String> validateCustomer(Customer customer) {
        return validateUserFields(customer.getUsername(), customer.getPassword(), customer.getEmail());
    }

    private Map<String, String> validateStaff(Staff staff) {
        return validateUserFields(staff.getUsername(), staff.getPassword(), staff.getEmail());
    }

    // Validation for Login
    private Map<String, String> validateLoginRequest(LoginRequest loginRequest) {
        return validateUserFields(loginRequest.getUsername(), loginRequest.getPassword(), null);
    }

    // Shared Validation Logic
    private Map<String, String> validateUserFields(String username, String password, String email) {
        Map<String, String> errors = new HashMap<>();

        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password is required");
        }
        if (email != null) { // Email is only validated for registration
            if (email.trim().isEmpty()) {
                errors.put("email", "Email is required");
            } else if (!isValidEmail(email)) {
                errors.put("email", "Invalid email format");
            }
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}