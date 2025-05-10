package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.repository.MovieRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Screen;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.ScreenRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepo;
    private final MovieRepository    movieRepo;
    private final ScreenRepository   screenRepo;

    public ShowtimeService(ShowtimeRepository showtimeRepo,
                           MovieRepository movieRepo,
                           ScreenRepository screenRepo) {
        this.showtimeRepo = showtimeRepo;
        this.movieRepo    = movieRepo;
        this.screenRepo   = screenRepo;
    }

    /* ---------- Query ---------- */
    public List<ShowtimeDto> listAll() {
        return showtimeRepo.findAll().stream().map(this::map).toList();
    }

    /* ---------- Mutation ---------- */
    @Transactional
    public ShowtimeDto create(ShowtimeCreateRequest dto) {
        Movie  movie  = movieRepo.findById(dto.movieId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Movie not found"));
        Screen screen = screenRepo.findById(dto.screenId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Screen not found"));

        Showtime s = new Showtime();
        s.setStartTime(dto.startTime());
        s.setEndTime(dto.endTime());
        s.setMovie(movie);
        s.setScreen(screen);

        return map(showtimeRepo.save(s));
    }

    @Transactional
    public ShowtimeDto update(Long id, ShowtimeUpdateRequest dto) {
        Showtime s = showtimeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Showtime not found"));

        s.setStartTime(dto.startTime());
        s.setEndTime(dto.endTime());
        return map(s);
    }

    @Transactional
    public void delete(Long id) {
        showtimeRepo.deleteById(id);
    }

    /* ---------- Mapper ---------- */
    private ShowtimeDto map(Showtime s) {
        return new ShowtimeDto(
                s.getId(),
                s.getStartTime(),
                s.getEndTime(),
                s.getMovie() != null  ? s.getMovie().getId()  : null,
                s.getScreen()!= null ? s.getScreen().getId() : null
        );
    }
}
