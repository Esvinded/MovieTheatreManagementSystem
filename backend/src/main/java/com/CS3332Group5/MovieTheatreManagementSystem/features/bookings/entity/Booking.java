// src/main/java/com/CS3332Group5/MovieTheatreManagementSystem/features/bookings/entity/Booking.java
package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime showtime;

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

    @Column(name = "movie_title_snapshot", nullable = false)
    private String movieTitleSnapshot;

    @Column(name = "start_time_snapshot", nullable = false)
    private java.time.OffsetDateTime startTimeSnapshot;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Payment payment;

    public Booking() {}

    public Booking(Customer customer, com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime showtime) {
        this.customer = customer;
        this.showtime = showtime;
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime getShowtime() { return showtime; }
    public BookingStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<BookingSeat> getSeats() { return seats; }
    public String getMovieTitleSnapshot() { return movieTitleSnapshot; }
    public java.time.OffsetDateTime getStartTimeSnapshot() { return startTimeSnapshot; }
    public Payment getPayment() { return payment; }

    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setShowtime(com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime showtime) { this.showtime = showtime; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setSeats(List<BookingSeat> seats) { this.seats = seats; }
    public void setMovieTitleSnapshot(String movieTitleSnapshot) { this.movieTitleSnapshot = movieTitleSnapshot; }
    public void setStartTimeSnapshot(java.time.OffsetDateTime startTimeSnapshot) { this.startTimeSnapshot = startTimeSnapshot; }
    public void setPayment(Payment payment) { this.payment = payment; }

    /** Thêm ghế vào booking, đảm bảo 2 chiều **/
    public void addSeat(BookingSeat seat) {
        seat.setBooking(this);
        seats.add(seat);
    }

    /** Xoá tất cả ghế có cùng seatId **/
    public void removeSeat(Long seatId) {
        seats.removeIf(s -> s.getSeat() != null && s.getSeat().getId().equals(seatId));
    }
}
