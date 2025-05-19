package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDto {
    private Long id;
    private LocalDateTime bookingDate;
    private String status;
    private List<BookingSeatDto> seats;
    private String movieTitle;
    private String screenName;
    private String theatreName;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<BookingSeatDto> getSeats() { return seats; }
    public void setSeats(List<BookingSeatDto> seats) { this.seats = seats; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }
}
