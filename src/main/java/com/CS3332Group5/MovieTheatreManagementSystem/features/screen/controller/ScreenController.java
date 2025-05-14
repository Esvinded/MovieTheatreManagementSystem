package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.service.ScreenService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.SeatDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
public class ScreenController {

    private final ScreenService service;

    public ScreenController(ScreenService service) {
        this.service = service;
    }

    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public ScreenDto create(@Valid @RequestBody ScreenCreateRequest r) {
        return service.create(r);
    }

    @GetMapping("/get")
    public List<ScreenDto> listAll() {
        return service.listAll();
    }

    /** GET /api/screens/get/theatre/{theatreId} */
    @GetMapping("/get/theatre/{theatreId}")
    public List<ScreenDto> byTheatre(@PathVariable Long theatreId) {
        return service.listByTheatre(theatreId);
    }

    /** GET /api/screens/{screenId}/seats  CHƯA LAM*/


    @PutMapping("/update/{id}")
    public ScreenDto update(@PathVariable Long id,
                            @Valid @RequestBody ScreenUpdateRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
