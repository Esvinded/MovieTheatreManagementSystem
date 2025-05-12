package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "theatres")
public class Theatre {

    /* ---------- FIELDS ---------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String address;

    /** OPEN / CLOSED */
    @NotBlank
    @Column(nullable = false)
    private String status;

    /** Số phòng chiếu – không cho UPDATE */
    @NotNull
    @Min(1)
    @Column(nullable = false, updatable = false)
    private Integer totalScreens;

    /* ---------- RELATIONS ---------- */

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Screen> screens = new HashSet<>();

    /* ---------- CONSTRUCTORS ---------- */

    public Theatre() { }

    public Theatre(String name, String address, String status, Integer totalScreens) {
        this.name = name;
        this.address = address;
        this.status = status;
        this.totalScreens = totalScreens;
    }

    /* ---------- GETTERS / SETTERS (không setter totalScreens) ---------- */

    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    public String getAddress()              { return address; }
    public void setAddress(String address)  { this.address = address; }

    public String getStatus()               { return status; }
    public void setStatus(String status)    { this.status = status; }

    public Integer getTotalScreens()        { return totalScreens; }

    public Set<Screen> getScreens()         { return screens; }
    public void setScreens(Set<Screen> screens) { this.screens = screens; }
}
