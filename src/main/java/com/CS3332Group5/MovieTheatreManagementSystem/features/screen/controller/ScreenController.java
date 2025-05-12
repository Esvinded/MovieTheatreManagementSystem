package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.ScreenCreateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.ScreenDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.service.ScreenService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.SeatDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screen")
public class ScreenController {

    private final ScreenService service;

    public ScreenController(ScreenService service) {
        this.service = service;
    }

    /* ---------- ENDPOINTS ---------- */

    /** GET /api/screen/get */
    @GetMapping("/get")
    public List<ScreenDto> all() {
        return service.listAll();
    }

    /** GET /api/screen/get/theatre/{theatreId} */
    @GetMapping("/get/theatre/{theatreId}")
    public List<ScreenDto> byTheatre(@PathVariable Long theatreId) {
        return service.listByTheatre(theatreId);
    }

    /** GET /api/screen/{screenId}/seats */
    @GetMapping("/{screenId}/seats")
    public List<SeatDto> seats(@PathVariable Long screenId) {
        return service.listSeatsByScreen(screenId);
    }

    /** POST /api/screen */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScreenDto create(@Valid @RequestBody ScreenCreateRequest r) {
        return service.create(r);
    }
}
