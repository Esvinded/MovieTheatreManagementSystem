// src/main/java/com/CS3332Group5/MovieTheatreManagementSystem/features/showtimes/repository/ShowtimeRepository.java
package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    // Scheduler post-showtime cleanup
    List<Showtime> findByEndTimeBefore(Instant now);
}
