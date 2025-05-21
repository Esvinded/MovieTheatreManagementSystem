// src/main/java/com/CS3332Group5/MovieTheatreManagementSystem/features/bookings/dto/CreateBookingRequest.java
package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateBookingRequest {
    @NotNull(message = "ShowtimeId không được để trống")
    private Long showtimeId;

    @NotNull(message = "Danh sách ghế không được để trống")
    @Size(min = 1, message = "Phải chọn ít nhất 1 ghế")
    private List<Long> seatIds;

    public CreateBookingRequest() {}

    public CreateBookingRequest(Long showtimeId, List<Long> seatIds) {
        this.showtimeId = showtimeId;
        this.seatIds = seatIds;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}
