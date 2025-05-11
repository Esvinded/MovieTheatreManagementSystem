package com.CS3332Group5.MovieTheatreManagementSystem.features.user.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class CustomerProfileDTO {
    private String username;
    private String email;

    @NotNull(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private LocalDate dateOfBirth;

    private String profileImageUrl;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }   

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    // Default Constructor
    public CustomerProfileDTO() {
    }

    // Constructor
    public CustomerProfileDTO(String username, String email, String fullName, String phoneNumber,
                              LocalDate dateOfBirth, String profileImageUrl) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.profileImageUrl = profileImageUrl;
    }

}
