package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screen")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService service;

    /** GET /api/screen/get */
    @GetMapping("/get")
    public List<ScreenDto> getAll() {
        return service.listAll();
    }

    /** POST /api/screen/set */
    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public ScreenDto setScreen(@RequestBody ScreenCreateRequest r) {
        return service.create(r);
    }

    /** PUT /api/screen/update/{id} */
    @PutMapping("/update/{id}")
    public ScreenDto updateScreen(
            @PathVariable Long id,
            @RequestBody ScreenUpdateRequest r
    ) {
        return service.update(id, r);
    }

    /** DELETE /api/screen/delete/{id} */
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScreen(@PathVariable Long id) {
        service.delete(id);
    }
}
