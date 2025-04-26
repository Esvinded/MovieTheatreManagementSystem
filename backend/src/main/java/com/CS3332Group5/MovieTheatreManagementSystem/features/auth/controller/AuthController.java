package com.CS3332Group5.MovieTheatreManagementSystem.features.auth.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.dto.ChangePasswordRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.dto.LoginRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Staff;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.service.UserService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.CustomerRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.StaffRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Optional<Customer> customerOptional = customerRepository.findByUsername(loginRequest.getUsername());
            if (customerOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            Customer customer = customerOptional.get();

            if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            // Authenticate with Spring Security
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                customer.getUsername(), null, new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok("Customer login successful");
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
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
    public ResponseEntity<?> loginStaff(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Optional<Staff> staffOptional = staffRepository.findByUsername(loginRequest.getUsername());
            if (staffOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            Staff staff = staffOptional.get();

            if (!passwordEncoder.matches(loginRequest.getPassword(), staff.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            // Authenticate with Spring Security
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                staff.getUsername(), null, new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok("Staff login successful");
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    // Unified Login for both Customer and Staff
    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
    //     // Validate login fields
    //     Map<String, String> errors = validateLoginRequest(loginRequest);
    //     if (!errors.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    //     }

    //     try {
    //         String loginMessage = userService.login(loginRequest);

    //         // After successful login, manually set Authentication (for session-based login)
    //         Authentication authentication = new UsernamePasswordAuthenticationToken(
    //             loginRequest.getUsername(), null, new ArrayList<>()
    //         );
    //         SecurityContextHolder.getContext().setAuthentication(authentication);
    //         request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

    //         return ResponseEntity.ok(loginMessage);
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    //     }
    // }

    // Change Password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String username = authentication.getName(); // Get username from Spring Security

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

        @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // get current session, don't create new
        if (session != null) {
            session.invalidate(); // destroy the session
        }
        return ResponseEntity.ok("Logged out successfully");
    }

}