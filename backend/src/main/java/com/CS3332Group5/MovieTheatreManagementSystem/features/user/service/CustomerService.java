package com.CS3332Group5.MovieTheatreManagementSystem.features.user.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.user.dto.CustomerProfileDTO;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private static final String UPLOAD_DIR = "uploads/";

    public CustomerProfileDTO getProfileByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return new CustomerProfileDTO(
            customer.getUsername(),
            customer.getEmail(),
            customer.getFullName(),
            customer.getPhoneNumber(),
            customer.getDateOfBirth(),
            customer.getProfileImageUrl()
        );
                
    }

    public void updateProfile(String username, CustomerProfileDTO updatedProfile) {
        try {
            System.out.println("Fetching customer by username: " + username);
            Customer customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            System.out.println("Updating customer details...");
            System.out.println("Full Name: " + updatedProfile.getFullName());
            System.out.println("Phone Number: " + updatedProfile.getPhoneNumber());
            System.out.println("Date of Birth: " + updatedProfile.getDateOfBirth());

            customer.setFullName(updatedProfile.getFullName());
            customer.setPhoneNumber(updatedProfile.getPhoneNumber());
            customer.setDateOfBirth(updatedProfile.getDateOfBirth());

            customerRepository.save(customer);
            System.out.println("Customer profile updated successfully.");
        } catch (Exception e) {
            System.out.println("Error in updateProfile: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace
            throw e;
        }
    }

    public void updateProfilePicture(String username, MultipartFile imageFile) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        String extension = Optional.ofNullable(imageFile.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(imageFile.getOriginalFilename().lastIndexOf(".")))
                .orElse("");

        String filename = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            String filepath = UPLOAD_DIR + filename;
            imageFile.transferTo(new File(filepath));

            customer.setProfileImageUrl("/" + filepath);
            customerRepository.save(customer);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload profile picture: " + e.getMessage(), e);
        }
    }
}
