package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "screens")
public class Screen extends BaseEntity {
    private Long id;

    private String name;
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;

    // --- Getters ---
    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getCapacity() { return capacity; }
    public Status getStatus() { return status; }
    public Theatre getTheatre() { return theatre; }
    public List<Seat> getSeats() { return seats; }

    // --- Setters ---
    public void setName(String name) {
        this.name = name;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public void setTheatre(Theatre theatre) {
        this.theatre = theatre;
    }
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
