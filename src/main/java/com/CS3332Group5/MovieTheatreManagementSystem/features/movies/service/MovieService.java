package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.service;

import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.repository.MovieRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepo;
    private final ShowtimeRepository showtimeRepo;

    public MovieService(MovieRepository movieRepo, ShowtimeRepository showtimeRepo) {
        this.movieRepo = movieRepo;
        this.showtimeRepo = showtimeRepo;
    }

    public List<MovieDto> listAll(){ return movieRepo.findAll().stream().map(this::map).toList();}
    @Transactional
    public MovieDto create(MovieCreateRequest r){
        if (movieRepo.existsByTitleIgnoreCase(r.getTitle()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Title is duplicated");

        // dùng khởi tạo thủ công vì builder
        Movie m = new Movie();
        m.setTitle(r.getTitle());
        m.setDuration(r.getDuration());
        m.setStatus(Status.ACTIVE);

        return map(movieRepo.save(m));
    }
    @Transactional
    public MovieDto update(Long id, MovieUpdateRequest r){
        Movie m=movieRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Not found"));
        m.setTitle(r.title()); m.setDuration(r.duration()); m.setStatus(r.status());
        return map(m);
    }
    @Transactional
    public void delete(Long id){
        if(showtimeRepo.existsByMovie_Id(id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Has showtimes");
        movieRepo.deleteById(id);
    }
    private MovieDto map(Movie m){return new MovieDto(m.getId(),m.getTitle(),m.getDuration(),m.getStatus());}
}