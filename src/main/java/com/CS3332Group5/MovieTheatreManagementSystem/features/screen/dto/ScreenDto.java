package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
public record ScreenDto(Long id, String name, Integer capacity, Status status) {}