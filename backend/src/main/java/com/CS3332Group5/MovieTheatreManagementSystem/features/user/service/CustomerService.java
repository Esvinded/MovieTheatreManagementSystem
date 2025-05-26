package com.CS3332Group5.MovieTheatreManagementSystem.features.user.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.user.dto.CustomerProfileDTO;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

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
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        customer.setFullName(updatedProfile.getFullName());
        customer.setPhoneNumber(updatedProfile.getPhoneNumber());
        customer.setDateOfBirth(updatedProfile.getDateOfBirth());

        customerRepository.save(customer);
    }

    public void updateProfilePicture(String username, String profileImageUrl) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    
        // Validate the URL (optional)
        if (profileImageUrl == null || !profileImageUrl.startsWith("http")) {
            throw new IllegalArgumentException("Invalid URL for profile picture");
        }
    
        // Update the customer's profile image URL
        customer.setProfileImageUrl(profileImageUrl);
        customerRepository.save(customer);
    }
}
