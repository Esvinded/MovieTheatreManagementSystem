package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtime")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService service;

    /** GET /api/showtime/get */
    @GetMapping("/get")
    public List<ShowtimeDto> getAll() {
        return service.listAll();
    }

    /** POST /api/showtime/set */
    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public ShowtimeDto setShowtime(@RequestBody ShowtimeCreateRequest r) {
        return service.create(r);
    }

    /** PUT /api/showtime/update/{id} */
    @PutMapping("/update/{id}")
    public ShowtimeDto updateShowtime(
            @PathVariable Long id,
            @RequestBody ShowtimeUpdateRequest r
    ) {
        return service.update(id, r);
    }

    /** DELETE /api/showtime/delete/{id} */
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShowtime(@PathVariable Long id) {
        service.delete(id);
    }
}
