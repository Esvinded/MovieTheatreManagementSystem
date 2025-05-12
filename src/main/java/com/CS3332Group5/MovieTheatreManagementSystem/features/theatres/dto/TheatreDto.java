package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto;

public class TheatreDto {

    private Long id;
    private String name;
    private String address;
    private String status;
    private Integer totalScreens;

    /* ---------- GETTERS / SETTERS ---------- */

    public Long getId()               { return id; }
    public void setId(Long id)        { this.id = id; }

    public String getName()           { return name; }
    public void setName(String name)  { this.name = name; }

    public String getAddress()        { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus()         { return status; }
    public void setStatus(String status)   { this.status = status; }

    public Integer getTotalScreens()  { return totalScreens; }
    public void setTotalScreens(Integer totalScreens) { this.totalScreens = totalScreens; }
}
