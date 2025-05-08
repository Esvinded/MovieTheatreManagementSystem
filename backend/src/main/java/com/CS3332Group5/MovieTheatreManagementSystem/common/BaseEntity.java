package com.CS3332Group5.MovieTheatreManagementSystem.common;

import jakarta.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}