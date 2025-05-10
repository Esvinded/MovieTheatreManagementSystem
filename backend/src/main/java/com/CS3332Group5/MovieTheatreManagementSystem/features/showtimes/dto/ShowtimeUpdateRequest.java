package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto;
import java.time.OffsetDateTime;
public record ShowtimeUpdateRequest(OffsetDateTime startTime, OffsetDateTime endTime){}