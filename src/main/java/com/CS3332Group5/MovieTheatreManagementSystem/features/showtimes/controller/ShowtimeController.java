package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/showtimes") @RequiredArgsConstructor
public class ShowtimeController {
    private final ShowtimeService service;
    @GetMapping public List<ShowtimeDto> all(){return service.listAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ShowtimeDto create(@RequestBody ShowtimeCreateRequest r){return service.create(r);}
    @PutMapping("/{id}") public ShowtimeDto update(@PathVariable Long id,@RequestBody ShowtimeUpdateRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
//Trong đó duration phải là ISO-8601 Duration (ví dụ "PT2H28M" nghĩa là 2 giờ 28 phút).
}