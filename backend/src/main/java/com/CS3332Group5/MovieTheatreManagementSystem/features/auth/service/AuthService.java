package com.CS3332Group5.MovieTheatreManagementSystem.features.auth.service;

import com.CS3332Group5.MovieTheatreManagementSystem.common.service.EmailService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.CustomerRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Staff;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Cooldown tracker
    private final Map<String, Instant> forgotPasswordTimestamps = new ConcurrentHashMap<>();
    private static final long COOLDOWN_SECONDS = 120; // 2 minutes

    public void processForgotPasswordByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be empty.");
        }

        Instant now = Instant.now();

        // Check cooldown
        Instant lastRequest = forgotPasswordTimestamps.get(username);
        if (lastRequest != null && now.isBefore(lastRequest.plusSeconds(COOLDOWN_SECONDS))) {
            throw new IllegalArgumentException("Please wait before trying again.");
        }

        // Update timestamp immediately
        forgotPasswordTimestamps.put(username, now);

        // Try to find user
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            resetPasswordAndSendEmail(customer.getEmail(), customer);
            return;
        }

        Optional<Staff> staffOpt = staffRepository.findByUsername(username);
        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            resetPasswordAndSendEmail(staff.getEmail(), staff);
            return;
        }

        // Not found
        throw new IllegalArgumentException("Username not found.");
    }

    private void resetPasswordAndSendEmail(String email, Object user) {
        String tempPassword = generateTempPassword(8);
        String encodedPassword = passwordEncoder.encode(tempPassword);

        if (user instanceof Customer) {
            ((Customer) user).setPassword(encodedPassword);
            customerRepository.save((Customer) user);
        } else if (user instanceof Staff) {
            ((Staff) user).setPassword(encodedPassword);
            staffRepository.save((Staff) user);
        }

        emailService.sendSimpleEmail(
                email,
                "Your Temporary Password",
                "Your new temporary password is: " + tempPassword +
                "\n\nPlease log in immediately and change your password!"
        );
    }

    private String generateTempPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}
