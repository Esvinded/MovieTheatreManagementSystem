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
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private static final String UPLOAD_DIR = "C:/uploads/";

    public CustomerProfileDTO getProfileByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        String profileImageUrl = customer.getProfileImageUrl();
            if (profileImageUrl == null || profileImageUrl.isEmpty()) {
                profileImageUrl = "/uploads/default-profile-picture.png"; // Use default if no profile picture is set
            }

        return new CustomerProfileDTO(
            customer.getUsername(),
            customer.getEmail(),
            customer.getFullName(),
            customer.getPhoneNumber(),
            customer.getDateOfBirth(),
            profileImageUrl
        );
    }

    public void updateProfile(String username, CustomerProfileDTO updatedProfile) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        customer.setFullName(updatedProfile.getFullName());
        customer.setPhoneNumber(updatedProfile.getPhoneNumber());
        customer.setDateOfBirth(updatedProfile.getDateOfBirth());

        customerRepository.save(customer);
    }

    public void updateProfilePicture(String username, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded or file is empty");
        }

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        String contentType = imageFile.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG and PNG are allowed.");
        }

        String extension = Optional.ofNullable(imageFile.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(imageFile.getOriginalFilename().lastIndexOf(".")))
                .orElseThrow(() -> new IllegalArgumentException("File must have a valid extension"));

        String filename = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR)); // Ensure the directory exists
            String filepath = UPLOAD_DIR + filename;

            imageFile.transferTo(new File(filepath));

            customer.setProfileImageUrl("/" + filepath);
            customerRepository.save(customer);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload profile picture: " + e.getMessage(), e);
        }
    }
}
