package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screens.entity.Screen;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.ScreenRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.ScreenCreateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.ScreenDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.ScreenUpdateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.TheatreRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ScreenService {

    private final ScreenRepository  repo;
    private final TheatreRepository theatreRepo;
    private final ShowtimeRepository showtimeRepo;

    public ScreenService(ScreenRepository repo,
                         TheatreRepository theatreRepo,
                         ShowtimeRepository showtimeRepo) {
        this.repo        = repo;
        this.theatreRepo = theatreRepo;
        this.showtimeRepo = showtimeRepo;
    }

    /* ---------- Query ---------- */
    public List<ScreenDto> listAll() {
        return repo.findAll().stream().map(this::map).toList();
    }

    /* ---------- Mutation ---------- */
    @Transactional
    public ScreenDto create(ScreenCreateRequest r) {
        Theatre th = theatreRepo.findById(r.theatreId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Theatre not found"));

        Screen s = new Screen();
        s.setName(r.name());
        s.setCapacity(r.capacity());
        s.setStatus(Status.ACTIVE);
        s.setTheatre(th);

        return map(repo.save(s));
    }

    @Transactional
    public ScreenDto update(Long id, ScreenUpdateRequest r) {
        Screen s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Screen not found"));

        /* nếu chuyển sang INACTIVE nhưng còn suất chiếu tương lai → chặn */
        if (r.status() == Status.INACTIVE
                && showtimeRepo.existsByScreen_IdAndStartTimeAfter(id, OffsetDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Has upcoming showtimes");
        }

        s.setName(r.name());
        s.setCapacity(r.capacity());
        s.setStatus(r.status());

        return map(s);
    }

    @Transactional
    public void delete(Long id) {
        if (showtimeRepo.existsByScreen_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Has showtimes");
        }
        repo.deleteById(id);
    }

    /* ---------- Mapper ---------- */
    private ScreenDto map(Screen s) {
        return new ScreenDto(
                s.getId(),
                s.getName(),
                s.getCapacity(),
                s.getStatus()
        );
    }
}
