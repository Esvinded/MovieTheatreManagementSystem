package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/theatres") @RequiredArgsConstructor
public class TheatreController {
    private final TheatreService service;
    @GetMapping public List<TheatreDto> all(){return service.listAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public TheatreDto create(@RequestBody TheatreCreateRequest r){return service.create(r);}
    @PutMapping("/{id}") public TheatreDto update(@PathVariable Long id,@RequestBody TheatreUpdateRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}