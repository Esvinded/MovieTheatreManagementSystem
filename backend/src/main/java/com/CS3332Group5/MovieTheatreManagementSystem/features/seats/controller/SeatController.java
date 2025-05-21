package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.SeatDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.SeatUpdateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService service;

    public SeatController(SeatService service) {
        this.service = service;
    }

    /** GET /api/seats/get - danh sách tất cả seats */
    @GetMapping("/get")
    public List<SeatDto> listAll() {
        return service.listAll();
    }

    /** GET /api/seats/{id} - chi tiết một seat */
    @GetMapping("/{id}")
    public SeatDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /** GET /api/seats/screen/{screenId} - seats theo screen */
    @GetMapping("/screen/{screenId}")
    public List<SeatDto> byScreen(@PathVariable Long screenId) {
        return service.listByScreen(screenId);
    }

    /** PUT /api/seats/update/{id} - chỉ update status */
    @PutMapping("/update/{id}")
    public SeatDto updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody SeatUpdateRequest r
    ) {
        return service.updateStatus(id, r);
    }

}
