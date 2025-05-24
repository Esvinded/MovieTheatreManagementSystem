package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.websocket;

public class SeatSelectionMessage {
    private Long showtimeId;
    private Long seatId;

    public Long getShowtimeId() { return showtimeId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
}
