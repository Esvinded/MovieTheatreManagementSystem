package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Một ghế ngồi trong một screen.
 */
@Entity
@Table(name = "seats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"screen_id", "row_label", "col_number"}))
public class Seat extends BaseEntity {

    @Min(1)
    @Column(name = "col_number", nullable = false)
    private int colNumber;            // số ghế trong hàng (1,2,3…)

    @NotNull
    @Column(name = "row_label", length = 5, nullable = false)
    private String rowLabel;          // ký hiệu hàng (A,B,C…)

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    /* ---------- constructors ---------- */
    public Seat() { }

    public Seat(int colNumber, String rowLabel, Status status, Screen screen) {
        this.colNumber = colNumber;
        this.rowLabel  = rowLabel;
        this.status    = status;
        this.screen    = screen;
    }

    /* ---------- getters & setters ---------- */
    public int getColNumber() {
        return colNumber;
    }

    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }
}
