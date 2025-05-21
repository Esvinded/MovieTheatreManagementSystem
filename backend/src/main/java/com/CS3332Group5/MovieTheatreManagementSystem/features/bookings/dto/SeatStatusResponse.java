package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;

/**
 * Trả về mã ghế và trạng thái của ghế đó cho frontend.
 */
public record SeatStatusResponse(String seatCode, SeatStatus status) {}
