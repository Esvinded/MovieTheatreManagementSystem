package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;

@Entity
@Table(name = "screens")
public class Screen {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int capacity;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    public Screen() {}
    public Screen(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}