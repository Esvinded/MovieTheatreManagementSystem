package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screens.entity.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "theatres")
public class Theatre extends BaseEntity {

    @NotBlank
    private String name;
    private String address;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Screen> screens = new HashSet<>();

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Set<Screen> getScreens() { return screens; }
    public void setScreens(Set<Screen> screens) { this.screens = screens; }
}