package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto;

import java.time.OffsetDateTime;

public record ShowtimeCreateRequest(
        Long movieId,
        Long screenId,
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {}
