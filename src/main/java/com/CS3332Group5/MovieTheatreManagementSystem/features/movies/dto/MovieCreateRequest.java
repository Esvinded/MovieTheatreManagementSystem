package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.dto;
import java.time.Duration;
public record MovieCreateRequest(String title, String description, Duration duration, Double rating) {
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }
}