package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.*;
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

    @GetMapping ("/get")
    public List<ShowtimeDto> listAll() {
        return service.listAll();
    }

    @PostMapping ("/set")
    public ShowtimeDto create(@RequestBody ShowtimeCreateRequest req) {
        return service.create(req);
    }
}
