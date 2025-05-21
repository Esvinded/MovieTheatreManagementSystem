package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsByTitleIgnoreCase(String title);
    Movie findMovieById(Long id);
//    List<Movie> findDistinctByShowtimes_Screen_Theater_Id(Long theaterId);
}
