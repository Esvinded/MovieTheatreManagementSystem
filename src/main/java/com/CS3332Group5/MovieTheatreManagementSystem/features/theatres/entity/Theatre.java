package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity;

import  com.CS3332Group5.MovieTheatreManagementSystem.common.enums.*;
import jakarta.persistence.*;

@Entity
@Table(name = "theatres")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    @Column(name = "total_screens", nullable = false)
    private Integer totalScreen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getTotalScreen() { return totalScreen; }
    public void setTotalScreen(Integer totalScreen) { this.totalScreen = totalScreen; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
