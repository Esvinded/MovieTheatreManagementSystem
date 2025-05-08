package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screens.entity.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Entity
@Table(name = "showtimes")
public class Showtime extends BaseEntity {

    @NotNull
    private OffsetDateTime startTime;
    @NotNull
    private OffsetDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    // getters & setters
    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }
}