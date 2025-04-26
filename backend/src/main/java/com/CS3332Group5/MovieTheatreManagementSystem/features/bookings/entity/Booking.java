// src/main/java/com/CS3332Group5/MovieTheatreManagementSystem/features/bookings/entity/Booking.java
package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "booking",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<BookingSeat> seats = new ArrayList<>();

    public Booking() {}

    public Booking(Long userId, Long showtimeId) {
        this.userId = userId;
        this.showtimeId = showtimeId;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getShowtimeId() { return showtimeId; }
    public BookingStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<BookingSeat> getSeats() { return seats; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setSeats(List<BookingSeat> seats) { this.seats = seats; }

    /** Thêm ghế vào booking, đảm bảo 2 chiều **/
    public void addSeat(BookingSeat seat) {
        seat.setBooking(this);
        seats.add(seat);
    }

    /** Xoá tất cả ghế có cùng code **/
    public void removeSeat(String seatCode) {
        seats.removeIf(s -> s.getSeatCode().equals(seatCode));
    }
}
