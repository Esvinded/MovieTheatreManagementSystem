package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/screens") @RequiredArgsConstructor
public class ScreenController {
    private final ScreenService service;
    @GetMapping public List<ScreenDto> all(){return service.listAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ScreenDto create(@RequestBody ScreenCreateRequest r){return service.create(r);}
    @PutMapping("/{id}") public ScreenDto update(@PathVariable Long id,@RequestBody ScreenUpdateRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}