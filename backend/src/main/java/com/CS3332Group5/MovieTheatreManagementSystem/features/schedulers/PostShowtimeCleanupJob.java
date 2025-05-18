package com.CS3332Group5.MovieTheatreManagementSystem.features.schedulers;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class PostShowtimeCleanupJob {

    private final ShowtimeRepository showtimeRepo;

    public PostShowtimeCleanupJob(ShowtimeRepository showtimeRepo) {
        this.showtimeRepo = showtimeRepo;
    }

    /** Runs at the top of every hour and deletes all past showtimes */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupPastShowtimes() {
        OffsetDateTime now = OffsetDateTime.now();
        showtimeRepo.deleteByStartTimeBefore(now);
    }
}
