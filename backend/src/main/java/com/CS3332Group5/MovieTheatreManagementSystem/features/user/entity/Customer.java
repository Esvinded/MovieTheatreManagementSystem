package com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer extends User {

    // No additional fields for now, but you can add customer-specific fields here

    // Default Constructor
    public Customer() {
    }

    // Constructor with inherited fields
    public Customer(String username, String password, String email) {
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
    }
}
