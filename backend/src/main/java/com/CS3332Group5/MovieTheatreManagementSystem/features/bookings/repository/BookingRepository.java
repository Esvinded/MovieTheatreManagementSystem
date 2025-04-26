// src/main/java/com/CS3332Group5/MovieTheatreManagementSystem/features/bookings/repository/BookingRepository.java
package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.Booking;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Tìm booking PENDING quá 5 phút → timeout
    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, Instant cutoff);

    // Scheduler post-showtime: booking còn AWAITING_PAYMENT sau showtime kết thúc
    List<Booking> findByShowtimeIdInAndStatus(List<Long> showtimeIds, BookingStatus status);

    // Kiểm tra seat đã RESERVE/BOOKED chưa để tránh double-booking
    @Query("""
      SELECT CASE WHEN COUNT(bs)>0 THEN TRUE ELSE FALSE END
      FROM Booking b JOIN b.seats bs
      WHERE b.showtimeId = :showtimeId
        AND bs.seatCode = :seatCode
        AND bs.status IN :statuses
      """)
    boolean existsByShowtimeIdAndSeatCodeAndStatuses(
      @Param("showtimeId") Long showtimeId,
      @Param("seatCode") String seatCode,
      @Param("statuses") List<SeatStatus> statuses
    );

    // Nếu muốn lock booking để đồng bộ (ví dụ khi confirm)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Booking findBookingById(Long id);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
