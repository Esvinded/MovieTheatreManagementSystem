package com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity;

import jakarta.persistence.Entity;

@Entity
public class Staff extends User {

    // No additional fields for now, but you can add staff-specific fields here

    // Default Constructor
    public Staff() {
    }

    // Constructor with inherited fields
    public Staff(String username, String password, String email) {
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
    }
}
