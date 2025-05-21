package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.service.ShowtimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    private final ShowtimeService svc;

    public ShowtimeController(ShowtimeService svc) {
        this.svc = svc;
    }

    /* CREATE ------------- */
    @PostMapping("/set")
    public ResponseEntity<ShowtimeDto> create(@RequestBody ShowtimeCreateRequest req) {
        return new ResponseEntity<>(svc.createShowtime(req), HttpStatus.CREATED);
    }

    /* UPDATE ------------- */
    @PutMapping("/update/{id}")
    public ResponseEntity<ShowtimeDto> update(
            @PathVariable Long id,
            @RequestBody ShowtimeUpdateRequest req) {

        Optional<ShowtimeDto> dto = svc.updateShowtime(id, req);
        return dto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /* LIST by movie */
    @GetMapping("/movie/{movieId}")
    public List<ShowtimeDto> listByMovie(@PathVariable Long movieId) {
        return svc.listByMovie(movieId);
    }

    /* LIST by screen */
    @GetMapping("/screen/{screenId}")
    public List<ShowtimeDto> listByScreen(@PathVariable Long screenId) {
        return svc.listByScreen(screenId);
    }
}
