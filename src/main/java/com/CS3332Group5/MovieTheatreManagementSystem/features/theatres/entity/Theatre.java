package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity.Screen;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theatres")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screen> screens = new ArrayList<>();

    /* Constructors */
    public Theatre() {}

    public Theatre(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /* Getters & Setters */
    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public List<Screen> getScreens() { return screens; }

    public void setScreens(List<Screen> screens) { this.screens = screens; }
}