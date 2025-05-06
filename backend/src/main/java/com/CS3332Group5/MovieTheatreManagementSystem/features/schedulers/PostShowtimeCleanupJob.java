package com.CS3332Group5.MovieTheatreManagementSystem.features.schedulers;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

@Component @RequiredArgsConstructor @Slf4j
public class PostShowtimeCleanupJob {
    private final ShowtimeRepository repo;
    @Scheduled(cron="0 0 */2 * * *") @Transactional
    public void cleanup(){
        OffsetDateTime cutoff=OffsetDateTime.now().minusDays(30);
        long count=repo.findAll().stream().filter(s->s.getEndTime().isBefore(cutoff)).peek(repo::delete).count();
        if(count>0) log.info("Cleaned {} old showtimes",count);
    }
}