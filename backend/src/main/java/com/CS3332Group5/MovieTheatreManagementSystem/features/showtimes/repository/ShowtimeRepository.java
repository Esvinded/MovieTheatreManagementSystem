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

    @Query("SELECT DISTINCT DATE(sh.startTime) FROM Showtime sh " +
        "WHERE sh.movie.id = :movieId AND sh.screen.theatre.id = :theatreId")
    List<LocalDate> findDistinctDatesByMovieAndTheatre(
        @Param("movieId") Long movieId,
        @Param("theatreId") Long theatreId
    );

    @Query("SELECT sh FROM Showtime sh " +
        "WHERE sh.movie.id = :movieId " +
        "AND sh.screen.theatre.id = :theatreId " +
        "AND DATE(sh.startTime) = :date")
    List<Showtime> findByMovieIdAndTheatreIdAndDate(
        @Param("movieId") Long movieId,
        @Param("theatreId") Long theatreId,
        @Param("date") LocalDate date
    );

}
