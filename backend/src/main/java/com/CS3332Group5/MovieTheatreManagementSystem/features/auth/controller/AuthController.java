package com.CS3332Group5.MovieTheatreManagementSystem.features.auth.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.dto.ChangePasswordRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.dto.LoginRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.dto.ForgotPasswordRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Staff;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.service.UserService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Customer Registration
    @PostMapping("/customer/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) {
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
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, String> errors = validateLoginRequest(loginRequest);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        try {
            String loginResponse = userService.loginCustomer(loginRequest, request);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // Staff Registration
    @PostMapping("/staff/register")
    public ResponseEntity<?> registerStaff(@RequestBody Staff staff) {
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
    public ResponseEntity<?> loginStaff(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, String> errors = validateLoginRequest(loginRequest);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        try {
            String loginResponse = userService.loginStaff(loginRequest, request);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // Change Password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String username = authentication.getName();

        try {
            userService.changePassword(username, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
        if (email != null) {
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // get current session, don't create new
        if (session != null) {
            session.invalidate(); // destroy the session
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPasswordByUsername(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.processForgotPasswordByUsername(request.getUsername());
            return ResponseEntity.ok("Temporary password has been sent to your email.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

}