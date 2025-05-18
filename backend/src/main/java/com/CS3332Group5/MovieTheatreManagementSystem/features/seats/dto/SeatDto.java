package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;

public class SeatDto {

    private Long id;
    private String rowLabel;
    private Integer colNumber;
    private String seatNumber;
    private Status status = Status.ACTIVE;
    private Long screenId;

    public SeatDto() {}

    public SeatDto(Long id, String rowLabel, Integer colNumber,
                   String seatNumber, Status status, Long screenId) {
        this.id = id;
        this.rowLabel = rowLabel;
        this.colNumber = colNumber;
        this.seatNumber = seatNumber;
        this.status = status;
        this.screenId = screenId;
    }

    // ==== Getters & Setters ====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }

    public Integer getColNumber() { return colNumber; }
    public void setColNumber(Integer colNumber) { this.colNumber = colNumber; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Long getScreenId() { return screenId; }
    public void setScreenId(Long screenId) { this.screenId = screenId; }
}
