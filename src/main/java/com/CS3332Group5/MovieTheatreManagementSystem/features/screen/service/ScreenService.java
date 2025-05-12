package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.SeatDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.ScreenCreateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.ScreenDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity.Screen;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.repository.ScreenRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.TheatreRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScreenService {

    private final ScreenRepository screenRepo;
    private final TheatreRepository theatreRepo;

    public ScreenService(ScreenRepository screenRepo, TheatreRepository theatreRepo) {
        this.screenRepo = screenRepo;
        this.theatreRepo = theatreRepo;
    }

    /* ---------- LISTING ---------- */

    public List<ScreenDto> listAll() {
        return screenRepo.findAll()
                .stream()
                .map(this::toScreenDto)
                .collect(Collectors.toList());
    }

    public List<ScreenDto> listByTheatre(Long theatreId) {
        return screenRepo.findByTheatreId(theatreId)
                .stream()
                .map(this::toScreenDto)
                .collect(Collectors.toList());
    }

    public List<SeatDto> listSeatsByScreen(Long screenId) {
        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screen not found"));

        return screen.getSeats()
                .stream()
                .map(this::toSeatDto)
                .collect(Collectors.toList());
    }

    /* ---------- CREATE ---------- */

    @Transactional
    public ScreenDto create(ScreenCreateRequest req) {

        Theatre theatre = theatreRepo.findById(req.getTheatreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Theatre not found"));

        long current = screenRepo.countByTheatreId(theatre.getId());
        if (current >= theatre.getTotalScreens()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Total screens exceeded: " + theatre.getTotalScreens());
        }

        Screen screen = new Screen();
        BeanUtils.copyProperties(req, screen);      // map từ request
        screen.setTheatre(theatre);

        return toScreenDto(screenRepo.save(screen));
    }

    /* ---------- PRIVATE HELPERS ---------- */

    private ScreenDto toScreenDto(Screen s) {
        ScreenDto dto = new ScreenDto();
        BeanUtils.copyProperties(s, dto);
        dto.setTheatreId(s.getTheatre().getId());   // nếu DTO có field này
        return dto;
    }

    private SeatDto toSeatDto(Seat seat) {
        SeatDto dto = new SeatDto();
        BeanUtils.copyProperties(seat, dto);
        return dto;
    }
}
