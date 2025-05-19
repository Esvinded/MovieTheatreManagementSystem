// src/main/java/com/CS3332Group5/MovieTheatreManagementSystem/features/bookings/entity/BookingSeat.java
package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "booking_seats")
public class BookingSeat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.RESERVED;

    @Column(name = "reserved_at", nullable = false, updatable = false)
    private Instant reservedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @Column(name = "price", nullable = false)
    private int price;

    public BookingSeat() {}

    public BookingSeat(com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat seat) {
        this.seat = seat;
    }

    public Long getId() { return id; }
    public com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat getSeat() { return seat; }
    public SeatStatus getStatus() { return status; }
    public Instant getReservedAt() { return reservedAt; }
    public Booking getBooking() { return booking; }
    public int getPrice() { return price; }

    public void setSeat(com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat seat) { this.seat = seat; }
    public void setStatus(SeatStatus status) { this.status = status; }
    public void setReservedAt(Instant reservedAt) { this.reservedAt = reservedAt; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public void setPrice(int price) { this.price = price; }
}
