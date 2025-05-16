package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    /** All future showtimes for a given movie */
    List<Showtime> findByMovieIdAndStartTimeAfter(Long movieId, OffsetDateTime now);

    /** All future showtimes for a given screen */
    List<Showtime> findByScreenIdAndStartTimeAfter(Long screenId, OffsetDateTime now);

    /** Delete all showtimes whose startTime is before the given cutoff */
    long deleteByStartTimeBefore(OffsetDateTime cutoff);

    /** Check whether any showtime exists for the given movie */
    boolean existsByMovieId(Long movieId);
}
