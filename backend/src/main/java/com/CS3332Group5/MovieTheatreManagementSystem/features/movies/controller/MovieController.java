package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    /**
     * Lấy danh sách tất cả phim
     * GET /api/movie/get
     */
    @GetMapping("/get")
    public List<MovieDto> getAll() {
        return service.listAll();
    }

    /**
     * Tạo mới một phim
     * POST /api/movie/set
     */
    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDto setMovie(@RequestBody MovieCreateRequest request) {
        return service.create(request);
    }

    /**
     * Cập nhật thông tin phim
     * PUT /api/movie/update/{id}
     */
    @PutMapping("/update/{id}")
    public MovieDto updateMovie(
            @PathVariable Long id,
            @RequestBody MovieUpdateRequest request
    ) {
        return service.update(id, request);
    }

    /**
     * Xóa phim
     * DELETE /api/movie/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable Long id) {
        service.delete(id);
    }
}
