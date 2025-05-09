package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto;
import java.time.OffsetDateTime;
public record ShowtimeDto(Long id, OffsetDateTime startTime, OffsetDateTime endTime, Long movieId, Long screenId){

}