package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Body JSON khi tạo ghế mới.
 */
public class SeatCreateRequest {

    @NotNull private Long    screenId;
    @NotBlank private String rowLabel;
    @Min(1)   private int    colNumber;
    @NotNull  private Status status;

    public SeatCreateRequest() { }

    public Long getScreenId() { return screenId; }
    public void setScreenId(Long screenId) { this.screenId = screenId; }

    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }

    public int getColNumber() { return colNumber; }
    public void setColNumber(int colNumber) { this.colNumber = colNumber; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
