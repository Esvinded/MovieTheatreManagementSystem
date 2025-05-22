package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.Booking;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingSeat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;
import java.util.List;

public class BookingMapper {
    private BookingMapper() {}

    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate() != null ? booking.getBookingDate().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        dto.setStatus(booking.getStatus().name());
        dto.setSeats(booking.getSeats().stream().map(BookingMapper::toSeatDto).toList());
        dto.setMovieTitle(booking.getMovieTitleSnapshot());
        // --- Populate screenName and theatreName ---
        String screenName = null;
        String theatreName = null;
        if (booking.getShowtime() != null && !booking.getSeats().isEmpty()) {
            BookingSeat firstSeat = booking.getSeats().get(0);
            if (firstSeat.getSeat() != null && firstSeat.getSeat().getScreen() != null) {
                screenName = firstSeat.getSeat().getScreen().getName();
                if (firstSeat.getSeat().getScreen().getTheatre() != null) {
                    theatreName = firstSeat.getSeat().getScreen().getTheatre().getName();
                }
            }
        }
        dto.setScreenName(screenName);
        dto.setTheatreName(theatreName);
        return dto;
    }

    public static BookingSeatDto toSeatDto(BookingSeat seat) {
        BookingSeatDto dto = new BookingSeatDto();
        Seat s = seat.getSeat();
        dto.setId(seat.getId());
        dto.setSeatId(s != null ? s.getId() : null); // Add actual seat id
        dto.setSeatNumber(s != null ? s.getSeatNumber() : null);
        dto.setSeatType(s != null ? s.getStatus().name() : null);
        dto.setAvailable(seat.getStatus().name().equals("RESERVED") || seat.getStatus().name().equals("BOOKED"));
        return dto;
    }

    public static List<BookingDto> toDtoList(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toDto).toList();
    }
}
