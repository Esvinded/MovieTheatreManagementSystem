package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.TheatreDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.TheatreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheatreService {

    private final TheatreRepository repo;

    public TheatreService(TheatreRepository repo) {
        this.repo = repo;
    }

    /* ---------- CREATE ---------- */

    @Transactional
    public TheatreDto create(TheatreDto dto) {
        Theatre entity = new Theatre(
                dto.getName(),
                dto.getAddress(),
                dto.getStatus(),
                dto.getTotalScreens()
        );
        Theatre saved = repo.save(entity);
        return toDto(saved);
    }

    /* ---------- LIST ---------- */

    public List<TheatreDto> listAll() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* ---------- PRIVATE MAPPER ---------- */

    private TheatreDto toDto(Theatre t) {
        TheatreDto dto = new TheatreDto();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setAddress(t.getAddress());
        dto.setStatus(t.getStatus());
        dto.setTotalScreens(t.getTotalScreens());
        return dto;
    }
}
