package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.time.LocalDate;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByMovieIdAndStartTimeAfter(Long movieId, OffsetDateTime now);
    List<Showtime> findByScreenIdAndStartTimeAfter(Long screenId, OffsetDateTime now);

    long deleteByStartTimeBefore(OffsetDateTime cutoff);

    boolean existsByMovieId(Long movieId);

    @Query(value = """
        SELECT DISTINCT DATE(sh.start_time)
        FROM showtimes sh
        JOIN screens s ON sh.screen_id = s.id
        WHERE sh.movie_id = :movieId AND s.theatre_id = :theatreId
    """, nativeQuery = true)
    List<java.sql.Date> findDistinctDatesByMovieAndTheatre(@Param("movieId") Long movieId, @Param("theatreId") Long theatreId);

    @Query(value = """
        SELECT sh.*
        FROM showtimes sh
        JOIN screens s ON sh.screen_id = s.id
        WHERE sh.movie_id = :movieId
        AND s.theatre_id = :theatreId
        AND DATE(sh.start_time) = :date
    """, nativeQuery = true)
    List<Showtime> findShowtimesByMovieAndTheatreAndDate(@Param("movieId") Long movieId, @Param("theatreId") Long theatreId, @Param("date") LocalDate date);    @Query("""
        SELECT s.id FROM Showtime s WHERE s.endTime < :cutoff AND NOT EXISTS (
            SELECT 1 FROM Booking b WHERE b.showtime = s AND b.status = 'BOOKED'
        )
    """)
    List<Long> findDeletableShowtimeIds(@Param("cutoff") OffsetDateTime cutoff);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("DELETE FROM Showtime s WHERE s.id IN :ids")
    int deleteShowtimesByIds(@Param("ids") List<Long> ids);
}
