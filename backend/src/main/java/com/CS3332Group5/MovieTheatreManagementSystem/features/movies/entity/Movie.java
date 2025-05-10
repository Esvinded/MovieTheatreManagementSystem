package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
public class Movie extends BaseEntity {

    @NotBlank
    private String title;
    private String description;

    @NotNull
    private Duration duration;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double rating;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Showtime> showtimes = new HashSet<>();

    // getters & setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Set<Showtime> getShowtimes() { return showtimes; }
    public void setShowtimes(Set<Showtime> showtimes) { this.showtimes = showtimes; }
}