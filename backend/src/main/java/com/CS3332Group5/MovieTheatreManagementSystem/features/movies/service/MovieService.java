package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.service;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.repository.MovieRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository  movieRepo;
    private final ShowtimeRepository showtimeRepo;

    public MovieService(MovieRepository movieRepo,
                        ShowtimeRepository showtimeRepo) {
        this.movieRepo   = movieRepo;
        this.showtimeRepo = showtimeRepo;
    }

    /* ---------- READ ---------- */
    public List<MovieDto> listAll() {
        return movieRepo.findAll().stream().map(this::map).toList();
    }

    /* ---------- CREATE ---------- */
    @Transactional
    public MovieDto create(MovieCreateRequest r) {
        Movie m = new Movie();
        m.setTitle      (r.getTitle());
        m.setDuration   (r.getDuration());
        m.setPosterURL  (r.getPosterURL());
        m.setStatus     (Status.ACTIVE);
        return map(movieRepo.save(m));
    }

    /* ---------- UPDATE ---------- */
    @Transactional
    public MovieDto update(Long id, MovieUpdateRequest r) {
        Movie m = movieRepo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        if (r.title()      != null) m.setTitle     (r.title());
        if (r.duration()   != null) m.setDuration  (r.duration());
        if (r.PosterURL()  != null) m.setPosterURL (r.PosterURL());
        if (r.status()     != null) m.setStatus    (r.status());
        return map(m);
    }

    /* ---------- DELETE ---------- */
    @Transactional
    public void delete(Long id) {
        if (showtimeRepo.existsByMovie_Id(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Movie has showtimes");
        movieRepo.deleteById(id);
    }

    /* ---------- MAPPER ---------- */
    private MovieDto map(Movie m) {
        return new MovieDto(
                m.getId(),
                m.getTitle(),
                m.getDuration(),
                m.getStatus(),
                m.getPosterURL()
        );
    }
}
