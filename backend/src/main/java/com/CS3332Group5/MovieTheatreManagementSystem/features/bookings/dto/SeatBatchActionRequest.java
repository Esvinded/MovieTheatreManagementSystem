package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto;

import java.util.List;

public class SeatBatchActionRequest {
    private List<Long> seatIds;

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}
