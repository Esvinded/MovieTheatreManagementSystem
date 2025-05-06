package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
public record TheatreUpdateRequest(String name,String address,Status status){}