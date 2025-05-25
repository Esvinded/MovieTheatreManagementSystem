// src/main/java/com/CS3332Group5/MovieTheatreManagementSystem/features/bookings/repository/BookingRepository.java
package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.Booking;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Tìm booking PENDING quá 5 phút → timeout
    List<Booking> findByStatusAndBookingDateBefore(BookingStatus status, Instant cutoff);

    // Scheduler post-showtime: booking còn AWAITING_PAYMENT sau showtime kết thúc
    List<Booking> findByShowtimeIdInAndStatus(List<Long> showtimeIds, BookingStatus status);

    // Kiểm tra seat đã RESERVE/BOOKED chưa để tránh double-booking
    @Query("""
      SELECT CASE WHEN COUNT(bs)>0 THEN TRUE ELSE FALSE END
      FROM Booking b JOIN b.seats bs
      WHERE b.showtime.id = :showtimeId
        AND bs.seat.rowLabel = :rowLabel
        AND bs.seat.colNumber = :colNumber
        AND bs.status IN :statuses
      """)
    boolean existsByShowtimeIdAndRowLabelAndColNumberAndStatuses(
      @Param("showtimeId") Long showtimeId,
      @Param("rowLabel") String rowLabel,
      @Param("colNumber") int colNumber,
      @Param("statuses") List<SeatStatus> statuses
    );

    // Nếu muốn lock booking để đồng bộ (ví dụ khi confirm)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Booking findBookingById(Long id);

    List<Booking> findByCustomerOrderByBookingDateDesc(Customer customer);

    // Efficient lookup for a user's PENDING booking for a showtime
    java.util.Optional<Booking> findByCustomerIdAndShowtimeIdAndStatus(Long customerId, Long showtimeId, BookingStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = 'EXPIRED' WHERE b.status IN ('PENDING', 'AWAITING_PAYMENT') AND b.bookingDate < :cutoff")
    int expireOldBookings(@Param("cutoff") java.time.Instant cutoff);

    @Modifying
    @Transactional
    @Query("UPDATE BookingSeat bs SET bs.status = 'RELEASED' WHERE bs.booking.id IN (SELECT b.id FROM Booking b WHERE b.status = 'EXPIRED' AND b.bookingDate < :cutoff)")
    int releaseSeatsForExpiredBookings(@Param("cutoff") java.time.Instant cutoff);

    @Modifying
    @Transactional
    @Query("DELETE FROM Booking b WHERE b.status IN ('EXPIRED', 'CANCELLED') AND b.bookingDate < :cutoff")
    int deleteExpiredOrCancelledBookings(@Param("cutoff") java.time.Instant cutoff);
}
