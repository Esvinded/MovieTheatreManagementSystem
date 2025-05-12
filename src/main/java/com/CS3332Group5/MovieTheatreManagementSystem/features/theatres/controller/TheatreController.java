package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.TheatreDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service.TheatreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatre")
public class TheatreController {

    private final TheatreService service;

    public TheatreController(TheatreService service) {
        this.service = service;
    }

    /* ---------- ENDPOINTS ---------- */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TheatreDto create(@Valid @RequestBody TheatreDto dto) {
        return service.create(dto);
    }

    @GetMapping("/get")
    public List<TheatreDto> all() {
        return service.listAll();
    }
}
