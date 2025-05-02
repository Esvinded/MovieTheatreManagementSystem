package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.*;
import java.time.OffsetDateTime;

@Entity @Table(name="showtimes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Showtime extends BaseEntity {
    @Future private OffsetDateTime startTime;
    @Future private OffsetDateTime endTime;
    @ManyToOne @JoinColumn(name="movie_id") private Movie movie;
    @ManyToOne @JoinColumn(name="screen_id") private Screen screen;
}