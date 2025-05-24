package com.CS3332Group5.MovieTheatreManagementSystem.features.schedulers;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service.BookingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingCleanupJob {
    private final BookingService bookingService;

    public BookingCleanupJob(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Runs every minute to expire old PENDING and AWAITING_PAYMENT bookings and release their seats.
     */
    @Scheduled(cron = "0 * * * * *")
    public void cleanupExpiredBookings() {
        bookingService.releaseExpired();
        bookingService.releaseExpiredAwaitingPayment();
    }
}
