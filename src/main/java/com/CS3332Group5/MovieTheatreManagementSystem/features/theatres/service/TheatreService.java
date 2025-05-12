package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.ScreenDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheatreService {

    private final TheatreRepository repo;

    /* constructor thủ công – thay @RequiredArgsConstructor */
    public TheatreService(TheatreRepository repo) {
        this.repo = repo;
    }

    /* ---------- Query ---------- */
    public List<TheatreDto> listAll() {
        return repo.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    /* ---------- Mutation ---------- */
    @Transactional
    public TheatreDto create(TheatreCreateRequest r) {
        Theatre t = new Theatre();
        t.setName(r.name());
        t.setAddress(r.address());
        t.setStatus(Status.ACTIVE);
        return map(repo.save(t));
    }

    @Transactional
    public TheatreDto update(Long id, TheatreUpdateRequest r) {
        Theatre t = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Not found"));

        t.setName(r.name());
        t.setAddress(r.address());
        t.setStatus(r.status());

        return map(t);
    }

    @Transactional
    public void delete(Long id) {
        Theatre t = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Not found"));

        if (t.getScreens() != null && !t.getScreens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Has screens");
        }
        repo.delete(t);
    }

    /* ---------- Mapper ---------- */
    private TheatreDto map(Theatre t) {
        var screens = t.getScreens() == null
                ? java.util.Set.<ScreenDto>of()
                : t.getScreens().stream()
                .map(s -> new ScreenDto(
                        s.getId(),
                        s.getName(),
                        s.getCapacity(),
                        s.getStatus()))
                .collect(Collectors.toSet());

        return new TheatreDto(
                t.getId(),
                t.getName(),
                t.getAddress(),
                t.getStatus(),
                screens
        );
    }
}
