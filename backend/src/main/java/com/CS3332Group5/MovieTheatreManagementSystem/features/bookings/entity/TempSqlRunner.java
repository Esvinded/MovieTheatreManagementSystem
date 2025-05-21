package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TempSqlRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void run() {
        try {
            jdbcTemplate.execute("ALTER TABLE bookings DROP COLUMN created_at;");
            System.out.println("Successfully dropped 'created_at' column from 'bookings' table.");
        } catch (Exception e) {
            System.err.println("Error running temp SQL: " + e.getMessage());
        }
    }
}
