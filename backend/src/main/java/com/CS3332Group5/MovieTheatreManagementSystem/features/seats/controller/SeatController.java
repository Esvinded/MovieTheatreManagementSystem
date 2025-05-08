package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat")
public class SeatController {

    private final SeatService service;

    public SeatController(SeatService service) {
        this.service = service;
    }

    /* ---------- CRUD ---------- */

    @GetMapping("/get")
    public List<SeatDto> all() {
        return service.listAll();
    }

    @GetMapping("/get/screen/{screenId}")
    public List<SeatDto> byScreen(@PathVariable Long screenId) {
        return service.listByScreen(screenId);
    }

    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public SeatDto create(@Valid @RequestBody SeatCreateRequest r) {
        return service.create(r);
    }

    @PutMapping("/update/{id}")
    public SeatDto update(@PathVariable Long id, @Valid @RequestBody SeatUpdateRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
