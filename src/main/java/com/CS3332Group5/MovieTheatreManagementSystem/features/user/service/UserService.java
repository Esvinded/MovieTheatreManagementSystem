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

@Service
public class UserService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Customer Registration
    public void registerCustomer(Customer customer) {
        if (customerRepository.findByUsername(customer.getUsername()).isPresent() ||
            customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or email already exists");
        }
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
    }

    // Customer Login
    public String loginCustomer(LoginRequest loginRequest) {
        Customer customer = customerRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return "Customer login successful! Welcome, " + customer.getUsername();
    }

    // Staff Registration
    public void registerStaff(Staff staff) {
        if (staffRepository.findByUsername(staff.getUsername()).isPresent() ||
            staffRepository.findByEmail(staff.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or email already exists");
        }
        staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        staffRepository.save(staff);
    }

    // Staff Login
    public String loginStaff(LoginRequest loginRequest) {
        Staff staff = staffRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), staff.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return "Staff login successful! Welcome, " + staff.getUsername();
    }
}