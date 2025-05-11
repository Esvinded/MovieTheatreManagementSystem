package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingSeat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    
    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.showtime.id = ?1 AND bs.status IN ?2")
    List<BookingSeat> findByShowtimeIdAndStatusIn(Long showtimeId, List<SeatStatus> statuses);
    
    @Query(
        "SELECT COUNT(bs) > 0 FROM BookingSeat bs WHERE bs.booking.showtime.id = ?1 AND bs.seat.rowLabel = ?2 AND bs.seat.colNumber = ?3 AND bs.status IN ?4"
    )
    boolean existsByShowtimeIdAndRowLabelAndColNumberAndStatusIn(
        Long showtimeId, 
        String rowLabel, 
        int colNumber, 
        List<SeatStatus> statuses
    );

    boolean existsByBooking_Showtime_IdAndSeat_IdAndStatusIn(Long showtimeId, Long seatId, List<SeatStatus> statuses);
}