package com.CS3332Group5.MovieTheatreManagementSystem.features.schedulers;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository.BookingRepository;
import java.time.Instant;
import java.time.Duration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingCleanupJob {
    private final BookingRepository bookingRepository;

    public BookingCleanupJob(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Runs every minute to expire old PENDING and AWAITING_PAYMENT bookings and release their seats.
     */
    @Scheduled(cron = "0 * * * * *")
    public void cleanupExpiredBookings() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(5));
        int bookingsUpdated = bookingRepository.expireOldBookings(cutoff);
        int seatsUpdated = bookingRepository.releaseSeatsForExpiredBookings(cutoff);
        // Optionally log the results
    }
}
