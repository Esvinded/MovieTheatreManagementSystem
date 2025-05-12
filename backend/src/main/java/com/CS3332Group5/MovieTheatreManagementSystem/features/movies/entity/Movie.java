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

    /* ---------- BASIC INFO ---------- */
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Duration duration;          // ISO-8601 (hh:mm:ss)

    @DecimalMin("0.0") @DecimalMax("10.0")
    private Double rating;

    /* ---------- NEW FIELD ---------- */
    /** URL đến poster (ảnh bìa phim). Có thể rỗng nếu chưa cập nhật */
    private String posterURL;

    /* ---------- STATUS & RELATIONS ---------- */
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Showtime> showtimes = new HashSet<>();

    /* ---------- GETTERS / SETTERS ---------- */
    public String getTitle()              { return title; }
    public void   setTitle(String title)  { this.title = title; }

    public String getDescription()        { return description; }
    public void   setDescription(String d){ this.description = d; }

    public Duration getDuration()         { return duration; }
    public void     setDuration(Duration d){ this.duration = d; }

    public Double getRating()             { return rating; }
    public void   setRating(Double r)     { this.rating = r; }

    public String getPosterURL()          { return posterURL; }
    public void   setPosterURL(String url){ this.posterURL = url; }

    public Status getStatus()             { return status; }
    public void   setStatus(Status s)     { this.status = s; }

    public Set<Showtime> getShowtimes()             { return showtimes; }
    public void          setShowtimes(Set<Showtime> s){ this.showtimes = s; }
}
