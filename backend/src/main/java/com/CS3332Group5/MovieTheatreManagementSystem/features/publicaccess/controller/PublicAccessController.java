package com.CS3332Group5.MovieTheatreManagementSystem.features.publicaccess.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.publicaccess.service.PublicAccessService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicAccessController {

    @Autowired
    private PublicAccessService publicAccessService;

    @GetMapping("/movies")
    public List<Movie> getAllMovies() {
        return publicAccessService.getAllMovies();
    }

    @GetMapping("/theatres")
    public List<Theatre> getTheatresForMovie(@RequestParam Long movieId) {
        return publicAccessService.getTheatresForMovie(movieId);
    }

    @GetMapping("/available-dates")
    public List<LocalDate> getAvailableDates(
        @RequestParam Long movieId,
        @RequestParam Long theatreId
    ) {
        return publicAccessService.getAvailableDates(movieId, theatreId);
    }

    @GetMapping("/showtimes")
    public List<Showtime> getShowtimes(
        @RequestParam Long movieId,
        @RequestParam Long theatreId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return publicAccessService.getShowtimes(movieId, theatreId, date);
    }
} 
