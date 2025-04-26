package com.CS3332Group5.MovieTheatreManagementSystem.features.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.auth.dto.LoginRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Staff;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.CustomerRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.StaffRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.common.service.EmailService;
import java.util.Random;


@Service
public class UserService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Register Customer
    public void registerCustomer(Customer customer) {
        validateRegistration(customer.getUsername(), customer.getEmail());
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
    }

    // Register Staff
    public void registerStaff(Staff staff) {
        validateRegistration(staff.getUsername(), staff.getEmail());
        staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        staffRepository.save(staff);
    }

    // Combined Login
    public String login(LoginRequest loginRequest) {
        Optional<Customer> customerOpt = customerRepository.findByUsername(loginRequest.getUsername());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                return "Customer login successful! Welcome, " + customer.getUsername();
            }
        }

        Optional<Staff> staffOpt = staffRepository.findByUsername(loginRequest.getUsername());
        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), staff.getPassword())) {
                return "Staff login successful! Welcome, " + staff.getUsername();
            }
        }

        throw new IllegalArgumentException("Invalid username or password");
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

    public void forgotPasswordAndSendEmail(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        Optional<Staff> staffOpt = staffRepository.findByEmail(email);
    
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            String tempPassword = generateTempPassword();
            customer.setPassword(passwordEncoder.encode(tempPassword));
            customerRepository.save(customer);
            emailService.sendSimpleEmail(
                email,
                "Your Temporary Password",
                "Your new temporary password is: " + tempPassword + "\nPlease login and change your password immediately."
            );
        } else if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            String tempPassword = generateTempPassword();
            staff.setPassword(passwordEncoder.encode(tempPassword));
            staffRepository.save(staff);
            emailService.sendSimpleEmail(
                email,
                "Your Temporary Password",
                "Your new temporary password is: " + tempPassword + "\nPlease login and change your password immediately."
            );
        } else {
            throw new IllegalArgumentException("Email not found in system");
        }
    }
    
    private String generateTempPassword() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
