package com.CS3332Group5.MovieTheatreManagementSystem.features.user.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.dto.LoginRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Staff;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.CustomerRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.StaffRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.common.service.EmailService;


@Service
public class UserService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Register Customer
    public void registerCustomer(Customer customer) {
        validateRegistration(customer.getUsername(), customer.getEmail());
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
    
        // Set default profile picture if not provided
        if (customer.getProfileImageUrl() == null || customer.getProfileImageUrl().isEmpty()) {
            customer.setProfileImageUrl("/uploads/default-profile-picture.png");
        }
        
        customerRepository.save(customer);
    }

    // Register Staff
    public void registerStaff(Staff staff) {
        validateRegistration(staff.getUsername(), staff.getEmail());
        staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        staffRepository.save(staff);
    }

    // Login Customer and Staff
    public String loginCustomer(LoginRequest loginRequest, HttpServletRequest request) {
        Customer customer = customerRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Assign ROLE_CUSTOMER
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customer.getUsername(), null, authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return "Customer login successful";
    }

    public String loginStaff(LoginRequest loginRequest, HttpServletRequest request) {
        Staff staff = staffRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), staff.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Assign ROLE_STAFF
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_STAFF"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                staff.getUsername(), null, authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return "Staff login successful";
    }

    // Change Password
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (detectUserType(username).equals("CUSTOMER")) {
            Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> 
                new IllegalArgumentException("User not found"));
            verifyAndChangePassword(customer, oldPassword, newPassword);
            customerRepository.save(customer);
        } else if (detectUserType(username).equals("STAFF")) {
            Staff staff = staffRepository.findByUsername(username).orElseThrow(() -> 
                new IllegalArgumentException("User not found"));
            verifyAndChangePassword(staff, oldPassword, newPassword);
            staffRepository.save(staff);
        }
    }

    // Helper: Password verify and set
    private void verifyAndChangePassword(Object user, String oldPassword, String newPassword) {
        String encodedPassword = (user instanceof Customer) ? 
            ((Customer) user).getPassword() : ((Staff) user).getPassword();

        if (!passwordEncoder.matches(oldPassword, encodedPassword)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (passwordEncoder.matches(newPassword, encodedPassword)) {
            throw new IllegalArgumentException("New password must be different from the old password");
        }

        String newEncoded = passwordEncoder.encode(newPassword);
        if (user instanceof Customer) {
            ((Customer) user).setPassword(newEncoded);
        } else if (user instanceof Staff) {
            ((Staff) user).setPassword(newEncoded);
        }
    }


    // Validation
    public void validateRegistration(String username, String email) {
        if (customerRepository.findByUsername(username).isPresent() ||
            staffRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (customerRepository.findByEmail(email).isPresent() ||
            staffRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    // Detect User Type
    public String detectUserType(String username) {
        if (customerRepository.findByUsername(username).isPresent()) {
            return "CUSTOMER";
        } else if (staffRepository.findByUsername(username).isPresent()) {
            return "STAFF";
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

}
