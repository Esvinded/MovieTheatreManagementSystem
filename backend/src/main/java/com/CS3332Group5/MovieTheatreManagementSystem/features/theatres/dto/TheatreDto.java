package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import java.util.Set;
public record TheatreDto(Long id, String name, String address, Status status, Set<ScreenDto> screens) {}