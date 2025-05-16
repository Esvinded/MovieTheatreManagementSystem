package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.ShowtimeDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.service.ShowtimeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    private final ShowtimeService service;

    public ShowtimeController(ShowtimeService service) {
        this.service = service;
    }

    /** GET /api/showtimes/get/movie/{movieId} */
    @GetMapping("/get/movie/{movieId}")
    public List<ShowtimeDto> getByMovie(@PathVariable Long movieId) {
        return service.listByMovie(movieId);
    }

    /** GET /api/showtimes/get/screen/{screenId} */
    @GetMapping("/get/screen/{screenId}")
    public List<ShowtimeDto> getByScreen(@PathVariable Long screenId) {
        return service.listByScreen(screenId);
    }
}
