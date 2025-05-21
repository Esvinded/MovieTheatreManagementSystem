package com.CS3332Group5.MovieTheatreManagementSystem.features.publicaccess.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.repository.MovieRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PublicAccessService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Theatre> getAllTheatres() {
        return theatreRepository.findAll();
    }

    public List<Theatre> getTheatresForMovie(Long movieId) {
        return theatreRepository.findTheatresWithShowtimesForMovie(movieId);
    }

    public List<LocalDate> getAvailableDates(Long movieId, Long theatreId) {
        List<java.sql.Date> sqlDates = showtimeRepository.findDistinctDatesByMovieAndTheatre(movieId, theatreId);
        return sqlDates.stream()
                .map(java.sql.Date::toLocalDate)
                .toList();
    }

    public List<Showtime> getShowtimes(Long movieId, Long theatreId, LocalDate date) {
        return showtimeRepository.findByMovieIdAndTheatreIdAndDate(movieId, theatreId, date);
    }

}
