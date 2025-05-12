package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.dto;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import java.time.Duration;
public record MovieUpdateRequest(String title, Duration duration, Status status, String PosterURL) {}