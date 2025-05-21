package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Payment() {}

    public Payment(Booking booking, Long amount, PaymentStatus status) {
        this.booking = booking;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() { return id; }
    public Booking getBooking() { return booking; }
    public Long getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setBooking(Booking booking) { this.booking = booking; }
    public void setAmount(Long amount) { this.amount = amount; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
