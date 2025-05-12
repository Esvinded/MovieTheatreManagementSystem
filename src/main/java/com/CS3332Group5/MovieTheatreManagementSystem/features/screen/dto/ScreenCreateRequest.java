package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto;

import jakarta.validation.constraints.NotNull;

public class ScreenCreateRequest {

    @NotNull
    private Long theatreId;

    @NotNull
    private String name;

    @NotNull
    private Integer screenNumber;

    /* ---------- GETTERS / SETTERS ---------- */
    public Long getTheatreId()             { return theatreId; }
    public void setTheatreId(Long theatreId) { this.theatreId = theatreId; }

    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }

    public Integer getScreenNumber()      { return screenNumber; }
    public void setScreenNumber(Integer screenNumber) { this.screenNumber = screenNumber; }
}
