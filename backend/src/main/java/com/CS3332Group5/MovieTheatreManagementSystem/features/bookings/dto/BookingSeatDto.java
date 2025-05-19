package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto;

public class BookingSeatDto {
    private Long id;
    private String seatNumber;
    private String seatType;
    private boolean isAvailable;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
