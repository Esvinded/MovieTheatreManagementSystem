package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "screens")
public class Screen extends BaseEntity {

    @NotBlank
    private String name;

    @Min(1)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    /* Relations */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seat> seats = new HashSet<>();

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Showtime> showtimes = new HashSet<>();

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Theatre getTheatre() { return theatre; }
    public void setTheatre(Theatre theatre) { this.theatre = theatre; }
    public Set<Seat> getSeats() { return seats; }
    public void setSeats(Set<Seat> seats) { this.seats = seats; }
    public Set<Showtime> getShowtimes() { return showtimes; }
    public void setShowtimes(Set<Showtime> showtimes) { this.showtimes = showtimes; }
}