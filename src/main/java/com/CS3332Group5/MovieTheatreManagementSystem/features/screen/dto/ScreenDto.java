package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto;

public class ScreenDto {

    private Long id;
    private String name;
    private Integer screenNumber;
    private Long theatreId;          // <— thêm

    /* ---------- GETTERS / SETTERS ---------- */
    public Long getId()                 { return id; }
    public void setId(Long id)          { this.id = id; }

    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }

    public Integer getScreenNumber()    { return screenNumber; }
    public void setScreenNumber(Integer screenNumber) { this.screenNumber = screenNumber; }

    public Long getTheatreId()          { return theatreId; }
    public void setTheatreId(Long theatreId) { this.theatreId = theatreId; }
}
