package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;

/**
 * DTO trả về cho client.
 */
public class SeatDto {

    private Long    id;
    private String  rowLabel;
    private int     colNumber;
    private Status  status;
    private Long    screenId;

    public SeatDto() { }

    public SeatDto(Long id, String rowLabel, int colNumber, Status status, Long screenId) {
        this.id        = id;
        this.rowLabel  = rowLabel;
        this.colNumber = colNumber;
        this.status    = status;
        this.screenId  = screenId;
    }

    /* getters & setters */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }

    public int getColNumber() { return colNumber; }
    public void setColNumber(int colNumber) { this.colNumber = colNumber; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Long getScreenId() { return screenId; }
    public void setScreenId(Long screenId) { this.screenId = screenId; }
}
