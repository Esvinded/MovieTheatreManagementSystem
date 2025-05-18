package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TheatreCreateRequest {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Address must not be blank")
    private String address;

    @NotNull(message = "TotalScreen is required")
    @Min(value = 1, message = "TotalScreen must be at least 1")
    private Integer totalScreen;

    public TheatreCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getTotalScreen() { return totalScreen; }
    public void setTotalScreen(Integer totalScreen) { this.totalScreen = totalScreen; }
}
