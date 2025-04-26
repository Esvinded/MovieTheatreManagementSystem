package com.CS3332Group5.MovieTheatreManagementSystem.features.schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service.BookingService;

@Component
public class BookingTimeoutScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingTimeoutScheduler.class);
    private final BookingService bookingService;

    public BookingTimeoutScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Chạy mỗi phút (mặc định) để:
     * 1) Expire các booking PENDING quá HOLD_DURATION (5 phút)
     * 2) Release tất cả seats trong booking đó
     */
    @Scheduled(fixedRateString = "${booking.cleanup.rate:60000}")
    public void cleanExpired() {
        log.debug("Running BookingTimeoutScheduler.cleanExpired()...");
        bookingService.releaseExpired();
    }
}
