package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.ScreenCreateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.ScreenDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.dto.ScreenUpdateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.service.ScreenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screen")
public class ScreenController {

    private final ScreenService service;

    public ScreenController(ScreenService service) {
        this.service = service;
    }

    /* ---------- READ ---------- */
    // GET /api/screen/get
    @GetMapping("/get")
    public List<ScreenDto> getAll() {
        return service.listAll();
    }

    /* ---------- CREATE ---------- */
    // POST /api/screen/set
    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public ScreenDto create(@RequestBody ScreenCreateRequest req) {
        return service.create(req);
    }

    /* ---------- UPDATE ---------- */
    // PUT /api/screen/update/{id}
    @PutMapping("/update/{id}")
    public ScreenDto update(@PathVariable Long id,
                            @RequestBody ScreenUpdateRequest req) {
        return service.update(id, req);
    }

    /* ---------- DELETE ---------- */
    // DELETE /api/screen/delete/{id}
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
