package com.CS3332Group5.MovieTheatreManagementSystem.features.publicaccess.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.publicaccess.service.PublicAccessService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicAccessController {

    @Autowired
    private PublicAccessService publicAccessService;

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(publicAccessService.getAllMovies());
    }

    @GetMapping("/theatres/all")
    public ResponseEntity<List<Theatre>> getAllTheatres() {
        return ResponseEntity.ok(publicAccessService.getAllTheatres());
    }

    @GetMapping("/theatres")
    public ResponseEntity<List<Theatre>> getTheatresForMovie(@RequestParam Long movieId) {
        return ResponseEntity.ok(publicAccessService.getTheatresForMovie(movieId));
    }

    @GetMapping("/available-dates")
    public ResponseEntity<List<LocalDate>> getAvailableDates(
            @RequestParam Long movieId,
            @RequestParam Long theatreId) {
        return ResponseEntity.ok(publicAccessService.getAvailableDates(movieId, theatreId));
    }

    @GetMapping("/showtimes")
    public ResponseEntity<List<Showtime>> getShowtimes(
            @RequestParam Long movieId,
            @RequestParam Long theatreId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(publicAccessService.getShowtimes(movieId, theatreId, date));
    }

}
