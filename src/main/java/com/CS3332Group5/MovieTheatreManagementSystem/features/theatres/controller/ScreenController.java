package com.CS3332Group5.MovieTheatreManagementSystem.features.screens.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service.ScreenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screen")
public class ScreenController {

    private final ScreenService service;

    /* constructor-injection cho field final */
    public ScreenController(ScreenService service) {
        this.service = service;
    }

    /* ---------- READ ---------- */
    @GetMapping
    public List<ScreenDto> getAll() {
        return service.listAll();
    }

    /* ---------- CREATE ---------- */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScreenDto create(@RequestBody ScreenCreateRequest req) {
        return service.create(req);
    }

    /* ---------- UPDATE ---------- */
    @PutMapping("/{id}")
    public ScreenDto update(@PathVariable Long id,
                            @RequestBody ScreenUpdateRequest req) {
        return service.update(id, req);
    }

    /* ---------- DELETE ---------- */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
