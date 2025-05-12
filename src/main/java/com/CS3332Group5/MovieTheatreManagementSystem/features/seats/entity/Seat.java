package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity.Screen;
import jakarta.persistence.*;

/**
 * Entity lưu thông tin ghế.
 */
@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rowLabel;           // Ký hiệu hàng (A, B, C …)

    @Column(name = "col_number")
    private int colNumber;             // Số ghế trong hàng

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")    // FK → screens.id
    private Screen screen;

    /* ---------- Constructors ---------- */
    public Seat() { }

    public Seat(String rowLabel, int colNumber, Status status, Screen screen) {
        this.rowLabel  = rowLabel;
        this.colNumber = colNumber;
        this.status    = status;
        this.screen    = screen;
    }

    /* ---------- Getters & Setters ---------- */
    public Long getId()                     { return id; }
    public String getRowLabel()             { return rowLabel; }
    public void setRowLabel(String rowLabel){ this.rowLabel = rowLabel; }
    public int getColNumber()               { return colNumber; }
    public void setColNumber(int colNumber) { this.colNumber = colNumber; }
    public Status getStatus()               { return status; }
    public void setStatus(Status status)    { this.status = status; }
    public Screen getScreen()               { return screen; }
    public void setScreen(Screen screen)    { this.screen = screen; }
}
