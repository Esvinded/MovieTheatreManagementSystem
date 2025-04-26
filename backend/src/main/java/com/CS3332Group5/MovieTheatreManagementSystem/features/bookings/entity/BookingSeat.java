// src/main/java/com/CS3332Group5/MovieTheatreManagementSystem/features/bookings/entity/BookingSeat.java
package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "booking_seats")
public class BookingSeat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "seat_code", nullable = false)
    private String seatCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.RESERVED;

    @Column(name = "reserved_at", nullable = false, updatable = false)
    private Instant reservedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    public BookingSeat() {}

    public BookingSeat(String seatCode) {
        this.seatCode = seatCode;
    }

    public Long getId() { return id; }
    public String getSeatCode() { return seatCode; }
    public SeatStatus getStatus() { return status; }
    public Instant getReservedAt() { return reservedAt; }
    public Booking getBooking() { return booking; }

    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }
    public void setStatus(SeatStatus status) { this.status = status; }
    public void setReservedAt(Instant reservedAt) { this.reservedAt = reservedAt; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
