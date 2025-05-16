package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.ShowtimeDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ShowtimeService {
    private final ShowtimeRepository repo;

    public ShowtimeService(ShowtimeRepository repo) {
        this.repo = repo;
    }

    public List<ShowtimeDto> listByMovie(Long movieId) {
        return repo.findByMovieIdAndStartTimeAfter(movieId, OffsetDateTime.now())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ShowtimeDto> listByScreen(Long screenId) {
        return repo.findByScreenIdAndStartTimeAfter(screenId, OffsetDateTime.now())
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ShowtimeDto toDto(Showtime e) {
        ShowtimeDto dto = new ShowtimeDto();
        dto.setId(e.getId());
        dto.setStartTime(e.getStartTime());
        dto.setEndTime(e.getEndTime());
        dto.setMovieId(e.getMovie().getId());
        dto.setScreenId(e.getScreen().getId());
        return dto;
    }
}
