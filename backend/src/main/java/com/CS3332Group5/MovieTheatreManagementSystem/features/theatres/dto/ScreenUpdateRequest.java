package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
public record ScreenUpdateRequest(String name,Integer capacity,Status status){}