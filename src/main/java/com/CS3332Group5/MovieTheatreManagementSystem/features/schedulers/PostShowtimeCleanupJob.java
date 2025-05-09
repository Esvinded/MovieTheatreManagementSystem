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

    /** Chạy mỗi giờ – xoá showtimes đã bắt đầu từ quá khứ */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupPastShowtimes() {
        showtimeRepo.deleteByStartTimeBefore(OffsetDateTime.now());
    }
}
