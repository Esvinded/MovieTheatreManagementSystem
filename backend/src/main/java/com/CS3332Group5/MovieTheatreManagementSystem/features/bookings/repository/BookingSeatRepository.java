package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingSeat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
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

    @Query("""
            SELECT COUNT(bs) > 0
            FROM BookingSeat bs
            WHERE bs.booking.showtime.id = :showtimeId
              AND bs.seat.id = :seatId
              AND bs.status IN :seatStatuses
              AND bs.booking.status IN :bookingStatuses
        """)
        boolean existsActiveSeat(
            @Param("showtimeId") Long showtimeId,
            @Param("seatId") Long seatId,
            @Param("seatStatuses") List<SeatStatus> seatStatuses,
            @Param("bookingStatuses") List<BookingStatus> bookingStatuses
        );

    @Modifying
    @Transactional
    @Query("DELETE FROM BookingSeat bs WHERE bs.status = 'RELEASED' AND bs.reservedAt < :cutoff")
    int deleteReleasedSeats(@Param("cutoff") java.time.Instant cutoff);
}