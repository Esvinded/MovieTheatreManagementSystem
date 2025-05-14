package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service.TheatreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {

    private final TheatreService service;

    public TheatreController(TheatreService service) {
        this.service = service;
    }

    /** Create mới + mặc định status = ACTIVE */
    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public TheatreDto create(@Valid @RequestBody TheatreCreateRequest req) {
        return service.create(req);
    }

    /** Lấy toàn bộ theatres */
    @GetMapping("/get")
    public List<TheatreDto> listAll() {
        return service.listAll();
    }

    /** Update theo id */
    @PutMapping("/update/{id}")
    public TheatreDto update(
            @PathVariable Long id,
            @Valid @RequestBody TheatreUpdateRequest req
    ) {
        return service.update(id, req);
    }
}
