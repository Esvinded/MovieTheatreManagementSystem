package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TheatreUpdateRequest {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Address must not be blank")
    private String address;


    public TheatreUpdateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }


}
