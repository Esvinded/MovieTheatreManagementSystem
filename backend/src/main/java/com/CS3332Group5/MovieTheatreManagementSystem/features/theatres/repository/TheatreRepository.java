package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    @Query(value = """
    SELECT DISTINCT t.* FROM theatres t
    JOIN screens s ON s.theatre_id = t.id
    JOIN showtimes st ON st.screen_id = s.id
    WHERE st.movie_id = :movieId
""", nativeQuery = true)
List<Theatre> findTheatresWithShowtimesForMovie(@Param("movieId") Long movieId);

}
