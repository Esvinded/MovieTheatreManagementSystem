package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import java.util.List;

public class ScreenDto {
    private Long id;
    private String name;
    private Integer capacity;
    private Status status;
    private Long theatreId;
    private List<String> seatNumbers;
    public ScreenDto(Long id, String name, Integer capacity, Status status, Long theatreId, List<String> seatNumbers) {
        this.id = id; this.name = name; this.capacity = capacity;
        this.status = status; this.theatreId = theatreId; this.seatNumbers = seatNumbers;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Long getTheatreId() { return theatreId; }
    public void setTheatreId(Long theatreId) { this.theatreId = theatreId; }
    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
}
