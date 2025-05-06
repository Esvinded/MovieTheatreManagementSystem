package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.dto;
import java.time.Duration;
public record MovieCreateRequest(String title, String description, Duration duration, Double rating) {}