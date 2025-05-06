package com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
public interface ShowtimeRepository extends JpaRepository<Showtime,Long>{
    boolean existsByMovie_Id(Long movieId);
    boolean existsByScreen_Id(Long screenId);
    boolean existsByScreen_IdAndStartTimeAfter(Long screenId, OffsetDateTime time);
}