package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.SeatDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.repository.SeatRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity.Screen;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.repository.ScreenRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.TheatreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScreenService {

    private final ScreenRepository screenRepo;
    private final TheatreRepository theatreRepo;
    private final SeatRepository seatRepo;

    public ScreenService(ScreenRepository screenRepo,
                         TheatreRepository theatreRepo,
                         SeatRepository seatRepo) {
        this.screenRepo = screenRepo;
        this.theatreRepo = theatreRepo;
        this.seatRepo = seatRepo;
    }

    @Transactional
    public ScreenDto create(ScreenCreateRequest r) {
        var th = theatreRepo.findById(r.getTheatreId())
                .orElseThrow(() -> new NoSuchElementException("Theatre not found"));
        Screen s = new Screen();
        s.setName(r.getName());
        s.setCapacity(r.getCapacity());
        s.setStatus(Status.ACTIVE);
        s.setTheatre(th);
        Screen saved = screenRepo.save(s);

        // generate seats
        generateSeats(saved);
        return toDto(saved);
    }

    public List<ScreenDto> listAll() {
        return screenRepo.findAll().stream().map(this::toDto).toList();
    }

    public List<ScreenDto> listByTheatre(Long theatreId) {
        return screenRepo.findByTheatreId(theatreId)
                .stream().map(this::toDto).toList();
    }


    @Transactional
    public ScreenDto update(Long id, ScreenUpdateRequest r) {
        var s = screenRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Screen not found"));
        s.setName(r.getName());
        s.setStatus(r.getStatus());
        Screen updated = screenRepo.save(s);
        return toDto(updated);
    }

    public void delete(Long id) {
        screenRepo.deleteById(id);
    }

    private void generateSeats(Screen s) {
        int cap = s.getCapacity();
        int rows = (int) Math.floor(Math.sqrt(cap));
        int cols = (int) Math.ceil((double) cap / rows);

        List<Seat> seats = new ArrayList<>(cap);
        char rowChar = 'A';
        int count = 0;

        for (int r = 0; r < rows && count < cap; r++, rowChar++) {
            for (int c = 1; c <= cols && count < cap; c++) {
                Seat seat = new Seat();
                String rowLabel = String.valueOf(rowChar);
                Integer colNumber = c;
                String seatNumber = rowLabel + colNumber;

                seat.setStatus(Status.ACTIVE);

                seat.setRowLabel(rowLabel);
                seat.setColNumber(colNumber);
                seat.setSeatNumber(seatNumber);
                seat.setScreen(s);

                seats.add(seat);
                count++;
            }
        }

        seatRepo.saveAll(seats);
        s.setSeats(seats);
    }

    private ScreenDto toDto(Screen s) {
        return new ScreenDto(
                s.getId(),
                s.getName(),
                s.getCapacity(),
                s.getStatus(),
                s.getTheatre().getId(),
                s.getSeats().stream()
                        .map(Seat::getSeatNumber)
                        .collect(Collectors.toList())
        );
    }
}
