package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.TheatreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheatreService {

    private final TheatreRepository repo;

    public TheatreService(TheatreRepository repo) {
        this.repo = repo;
    }

    public TheatreDto create(TheatreCreateRequest r) {
        Theatre t = new Theatre();
        t.setName(r.getName());
        t.setAddress(r.getAddress());
        t.setTotalScreen(r.getTotalScreen());
        t.setStatus(Status.ACTIVE);
        Theatre saved = repo.save(t);
        return toDto(saved);
    }

    public List<TheatreDto> listAll() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public TheatreDto update(Long id, TheatreUpdateRequest r) {
        Theatre t = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Theatre not found: " + id));
        t.setName(r.getName());
        t.setAddress(r.getAddress());
        // giữ nguyên status cũ hoặc có thể thay đổi nếu cần
        Theatre saved = repo.save(t);
        return toDto(saved);
    }

    private TheatreDto toDto(Theatre e) {
        return new TheatreDto(
                e.getId(),
                e.getName(),
                e.getAddress(),
                e.getTotalScreen(),
                e.getStatus()
        );
    }
}
