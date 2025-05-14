package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import jakarta.validation.constraints.NotNull;

public class SeatUpdateRequest {

    @NotNull
    private Status status;

    public SeatUpdateRequest() {}

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
