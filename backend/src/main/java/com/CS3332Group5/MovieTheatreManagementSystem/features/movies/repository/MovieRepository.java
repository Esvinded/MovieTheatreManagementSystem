package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.repository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsByTitleIgnoreCase(String title);
}