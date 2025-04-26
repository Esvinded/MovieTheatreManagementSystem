package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "theatre_id", nullable = false)
    private Long theatreId;

    @NotNull
    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    public Showtime() {}

    public Showtime(Long theatreId, Long movieId, Instant startTime, Instant endTime) {
        this.theatreId = theatreId;
        this.movieId   = movieId;
        this.startTime = startTime;
        this.endTime   = endTime;
    }

    public Long getId() {
        return id;
    }

    public Long getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(Long theatreId) {
        this.theatreId = theatreId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
