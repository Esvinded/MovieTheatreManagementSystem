package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/movies") @RequiredArgsConstructor
public class MovieController {
    private final MovieService service;
    @GetMapping public List<MovieDto> all(){ return service.listAll(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public MovieDto create(@RequestBody MovieCreateRequest r){ return service.create(r); }
    @PutMapping("/{id}") public MovieDto update(@PathVariable Long id,@RequestBody MovieUpdateRequest r){ return service.update(id,r); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){ service.delete(id); }
}