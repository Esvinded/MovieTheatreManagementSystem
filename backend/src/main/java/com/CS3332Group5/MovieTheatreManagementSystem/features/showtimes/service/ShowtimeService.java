package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    private final ShowtimeRepository repo;

    public ShowtimeService(ShowtimeRepository repo) {
        this.repo = repo;
    }

    /* ---------- CREATE ---------- */
    public ShowtimeDto createShowtime(ShowtimeCreateRequest req) {
        Showtime e = new Showtime();
        e.setMovieId(req.movieId());
        e.setScreenId(req.screenId());
        e.setStartTime(req.startTime());
        e.setEndTime(req.endTime());

        e = repo.save(e);
        return toDto(e);
    }

    /* ---------- UPDATE ---------- */
    public Optional<ShowtimeDto> updateShowtime(Long id, ShowtimeUpdateRequest req) {
        return repo.findById(id)
                .map(e -> {
                    if (req.startTime() != null) e.setStartTime(req.startTime());
                    if (req.endTime()   != null) e.setEndTime(req.endTime());
                    return repo.save(e);
                })
                .map(this::toDto);
    }

    /* ---------- LIST helpers ---------- */
    public List<ShowtimeDto> listByMovie(Long movieId) {
        return repo.findByMovieIdAndStartTimeAfter(movieId, OffsetDateTime.now())
                .stream().map(this::toDto).toList();
    }

    public List<ShowtimeDto> listByScreen(Long screenId) {
        return repo.findByScreenIdAndStartTimeAfter(screenId, OffsetDateTime.now())
                .stream().map(this::toDto).toList();
    }

    /* ---------- mapper ---------- */
    private ShowtimeDto toDto(Showtime e) {
        return new ShowtimeDto(
                e.getId(),
                e.getMovieId(),
                e.getScreenId(),
                e.getStartTime(),
                e.getEndTime()
        );
    }
}
