package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity.Screen;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat extends BaseEntity {

    private Long id;

    @Column(name = "row_label", nullable = false)
    private String rowLabel;

    @Column(name = "col_number", nullable = false)
    private Integer colNumber;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    // ==== Getters & Setters ====

    public Long getId() { return id; }

    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }

    public Integer getColNumber() { return colNumber; }
    public void setColNumber(Integer colNumber) { this.colNumber = colNumber; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }
}
