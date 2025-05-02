package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.repository.MovieRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.ScreenRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.dto.*;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository repo;
    private final MovieRepository movieRepo;
    private final ScreenRepository screenRepo;
    public List<ShowtimeDto> listAll(){return repo.findAll().stream().map(this::map).toList();}
    @Transactional public ShowtimeDto create(ShowtimeCreateRequest r){
        var movie=movieRepo.findById(r.movieId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Movie not found"));
        var screen=screenRepo.findById(r.screenId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Screen not found"));
        boolean overlap=repo.findAll().stream()
            .anyMatch(s->s.getScreen().equals(screen)&&s.getStartTime().isBefore(r.endTime())&&r.startTime().isBefore(s.getEndTime()));
        if(overlap) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Overlap");
        Showtime s=Showtime.builder().startTime(r.startTime()).endTime(r.endTime()).movie(movie).screen(screen).build();
        return map(repo.save(s));
    }
    @Transactional public ShowtimeDto update(Long id, ShowtimeUpdateRequest r){
        Showtime s=repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Not found"));
        s.setStartTime(r.startTime()); s.setEndTime(r.endTime()); return map(s);
    }
    @Transactional public void delete(Long id){repo.deleteById(id);}
    private ShowtimeDto map(Showtime s){return new ShowtimeDto(s.getId(),s.getStartTime(),s.getEndTime(),s.getMovie().getId(),s.getScreen().getId());}
}