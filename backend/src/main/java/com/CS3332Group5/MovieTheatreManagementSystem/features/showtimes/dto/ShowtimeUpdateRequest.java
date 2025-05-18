package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto;

import java.time.OffsetDateTime;

/** Only allow updating time fields */
public record ShowtimeUpdateRequest(
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {}
