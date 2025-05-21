package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScreenUpdateRequest {
    @NotBlank private String name;
    @NotNull private Status status;

    public ScreenUpdateRequest() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
