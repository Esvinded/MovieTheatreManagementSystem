package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
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

    /** foreign keys saved as simple IDs */
    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "screen_id", nullable = false)
    private Long screenId;

    /* ------- getters / setters ------- */
    public OffsetDateTime getStartTime()          { return startTime; }
    public void setStartTime(OffsetDateTime t)    { this.startTime = t; }

    public OffsetDateTime getEndTime()            { return endTime; }
    public void setEndTime(OffsetDateTime t)      { this.endTime = t; }

    public Long getMovieId()                      { return movieId; }
    public void setMovieId(Long movieId)          { this.movieId = movieId; }

    public Long getScreenId()                     { return screenId; }
    public void setScreenId(Long screenId)        { this.screenId = screenId; }
}
