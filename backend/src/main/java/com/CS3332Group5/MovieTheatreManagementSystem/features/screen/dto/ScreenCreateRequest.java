package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScreenCreateRequest {
    @NotBlank private String name;
    @NotNull @Min(1) private Integer capacity;
    @NotNull private Long theatreId;

    public ScreenCreateRequest() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Long getTheatreId() { return theatreId; }
    public void setTheatreId(Long theatreId) { this.theatreId = theatreId; }
}
