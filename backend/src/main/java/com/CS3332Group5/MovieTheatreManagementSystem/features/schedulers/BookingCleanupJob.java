package com.CS3332Group5.MovieTheatreManagementSystem.features.schedulers;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository.BookingRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository.BookingSeatRepository;
import java.time.Instant;
import java.time.Duration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingCleanupJob {
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public BookingCleanupJob(BookingRepository bookingRepository, BookingSeatRepository bookingSeatRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    /**
     * Runs every minute to expire old PENDING and AWAITING_PAYMENT bookings and release their seats.
     */
    @Scheduled(cron = "0 * * * * *")
    public void cleanupExpiredBookings() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(5)); // revert to 5 minutes for status change
        int bookingsUpdated = bookingRepository.expireOldBookings(cutoff);
        int seatsUpdated = bookingRepository.releaseSeatsForExpiredBookings(cutoff);
        // Optionally log the results
    }

    /**
     * Runs every 3 minutes to delete expired/cancelled bookings and released booking seats.
     */
    @Scheduled(cron = "0 */3 * * * *")
    public void cleanupExpiredAndReleased() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(10)); // keep 10 minutes for actual deletion
        bookingSeatRepository.deleteReleasedSeats(cutoff);
        bookingRepository.deleteExpiredOrCancelledBookings(cutoff);
        // Optionally log the results
    }
}
