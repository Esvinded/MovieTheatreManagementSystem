package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.repository.SeatRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Screen;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.ScreenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatRepository seatRepo;
    private final ScreenRepository screenRepo;

    public SeatService(SeatRepository seatRepo, ScreenRepository screenRepo) {
        this.seatRepo   = seatRepo;
        this.screenRepo = screenRepo;
    }

    public List<SeatDto> listAll() {
        return seatRepo.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public List<SeatDto> listByScreen(Long screenId) {
        return seatRepo.findByScreen_Id(screenId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeatDto create(SeatCreateRequest r) {
        Screen screen = screenRepo.findById(r.getScreenId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screen not found"));
        Seat seat = new Seat(r.getColNumber(), r.getRowLabel(), r.getStatus(), screen);
        return map(seatRepo.save(seat));
    }

    @Transactional
    public SeatDto update(Long id, SeatUpdateRequest r) {
        Seat seat = seatRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        seat.setColNumber(r.getColNumber());
        seat.setRowLabel(r.getRowLabel());
        seat.setStatus(r.getStatus());
        return map(seat);
    }

    @Transactional
    public void delete(Long id) {
        if (!seatRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found");
        }
        seatRepo.deleteById(id);
    }

    /* ---- helper ---- */
    private SeatDto map(Seat s) {
        return new SeatDto(
                s.getId(),
                s.getRowLabel(),
                s.getColNumber(),
                s.getStatus(),
                s.getScreen().getId()
        );
    }
}
