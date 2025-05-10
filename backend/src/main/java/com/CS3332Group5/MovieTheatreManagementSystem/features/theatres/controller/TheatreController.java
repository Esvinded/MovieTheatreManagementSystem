package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.controller;


import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.TheatreCreateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.TheatreDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service.TheatreService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {
    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @GetMapping
    public List<TheatreDto> listAll() {
        return theatreService.listAll();
    }

    @PostMapping
    public TheatreDto create(@RequestBody TheatreCreateRequest dto) {
        return theatreService.create(dto);
    }
}