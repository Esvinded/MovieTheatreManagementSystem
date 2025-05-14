package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.*;

public class TheatreDto {

    private Long id;
    private String name;
    private String address;
    private Integer totalScreen;
    private Status status;

    public TheatreDto() {}

    public TheatreDto(Long id, String name, String address, Integer totalScreen, Status status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.totalScreen = totalScreen;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getTotalScreen() { return totalScreen; }
    public void setTotalScreen(Integer totalScreen) { this.totalScreen = totalScreen; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
