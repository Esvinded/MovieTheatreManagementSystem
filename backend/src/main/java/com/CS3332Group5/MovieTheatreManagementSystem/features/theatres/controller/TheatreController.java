package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatre")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService service;

    /** GET /api/theatre/get */
    @GetMapping("/get")
    public List<TheatreDto> getAll() {
        return service.listAll();
    }

    /** POST /api/theatre/set */
    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public TheatreDto setTheatre(@RequestBody TheatreCreateRequest r) {
        return service.create(r);
    }

    /** PUT /api/theatre/update/{id} */
    @PutMapping("/update/{id}")
    public TheatreDto updateTheatre(
            @PathVariable Long id,
            @RequestBody TheatreUpdateRequest r
    ) {
        return service.update(id, r);
    }

    /** DELETE /api/theatre/delete/{id} */
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheatre(@PathVariable Long id) {
        service.delete(id);
    }
}
