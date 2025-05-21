package com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer extends User {
    @Column(nullable = true)
    private String fullName;
    
    @Column(nullable = true)
    private String phoneNumber;
    
    @Column(nullable = true)
    private LocalDate dateOfBirth;
    
    @Column(nullable = true)
    private String profileImageUrl; // store image URL or filename

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
    public Customer() {
    }

    // Constructor with inherited fields
    public Customer(String username, String password, String email, String fullName, String phoneNumber,
                    LocalDate dateOfBirth, String profileImageUrl) {
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.profileImageUrl = profileImageUrl;
    }
}
