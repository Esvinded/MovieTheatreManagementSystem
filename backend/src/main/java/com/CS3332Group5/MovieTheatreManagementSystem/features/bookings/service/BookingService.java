package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.CreateBookingRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.Booking;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingSeat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository.BookingRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository.BookingSeatRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingNotificationService notificationService;

    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    /**
     * Tìm booking theo id
     */
    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        return bookingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking không tồn tại"));
    }

    /**
     * Bắt đầu 1 booking mới: hold ghế (PENDING)
     */
    @Transactional
    public Booking startBooking(CreateBookingRequest req, Long userId) {
        Long showtimeId = req.getShowtimeId();
        // Validate showtime tồn tại và chưa chiếu
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Suất chiếu với id=" + showtimeId + " không tồn tại"
            );
        }

        // Tạo booking trạng thái PENDING
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setShowtimeId(showtimeId);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(Instant.now());
        booking = bookingRepository.save(booking);

        // Hold từng ghế (RESERVED)
        for (String code : req.getSeatCodes()) {
            // Kiểm double-booking
            boolean exists = bookingSeatRepository.existsByShowtimeIdAndSeatCodeAndStatusIn(
                showtimeId, code,
                List.of(SeatStatus.RESERVED, SeatStatus.BOOKED)
            );
            if (exists) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ghế " + code + " đã có người giữ/đặt"
                );
            }
            BookingSeat seat = new BookingSeat();
            seat.setBooking(booking);
            seat.setSeatCode(code);
            seat.setStatus(SeatStatus.RESERVED);
            seat.setReservedAt(Instant.now());
            booking.getSeats().add(seat);
        }
        
        // Notify seat status changed
        notificationService.notifySeatStatusChanged(showtimeId, booking.getSeats());
        
        return booking;
    }

    /**
     * Toggle chọn/hủy chọn ghế (chỉ khi PENDING)
     */
    @Transactional
    public Booking toggleSeat(Long bookingId, String seatCode) {
        Booking booking = findById(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể chỉnh ghế khi đang PENDING");
        }
        
        // Nếu đã có trong list thì remove (deselect)
        boolean removed = booking.getSeats().removeIf(s -> s.getSeatCode().equals(seatCode));
        if (!removed) {
            // Nếu chưa có thì thêm (select) với trạng thái RESERVED
            if (bookingSeatRepository.existsByShowtimeIdAndSeatCodeAndStatusIn(
                booking.getShowtimeId(), seatCode,
                List.of(SeatStatus.RESERVED, SeatStatus.BOOKED)
            )) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ghế "+seatCode+" đã có người giữ/đặt");
            }
            BookingSeat seat = new BookingSeat();
            seat.setBooking(booking);
            seat.setSeatCode(seatCode);
            seat.setStatus(SeatStatus.RESERVED);
            seat.setReservedAt(Instant.now());
            booking.getSeats().add(seat);
        }

        // Notify seat status changed
        notificationService.notifySeatStatusChanged(booking.getShowtimeId(), booking.getSeats());
        
        return booking;
    }

    /**
     * Xác nhận booking (PENDING -> AWAITING_PAYMENT)
     */
    @Transactional
    public Booking confirm(Long bookingId) {
        Booking booking = findById(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ PENDING mới có thể xác nhận");
        }
        booking.setStatus(BookingStatus.AWAITING_PAYMENT);
        return booking;
    }

    /**
     * Lấy trạng thái ghế cho 1 showtime (reserved/booked)
     */
    @Transactional(readOnly = true)
    public List<BookingSeat> getSeatStatuses(Long showtimeId) {
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Suất chiếu không tồn tại");
        }
        return bookingSeatRepository.findByShowtimeIdAndStatusIn(
            showtimeId,
            List.of(SeatStatus.RESERVED, SeatStatus.BOOKED)
        );
    }

    /**
     * Scheduler: release các booking PENDING quá HOLD_DURATION
     */
    @Transactional
    public void releaseExpired() {
        Instant cutoff = Instant.now().minus(HOLD_DURATION);
        List<Booking> olds = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoff);
        olds.forEach(b -> {
            b.setStatus(BookingStatus.EXPIRED);
            b.getSeats().forEach(s -> s.setStatus(SeatStatus.RELEASED));
            
            // Notify booking expired
            notificationService.notifyBookingExpired(b.getId());
            // Notify seat status changed
            notificationService.notifySeatStatusChanged(b.getShowtimeId(), b.getSeats());
        });
    }

    /**
     * Hủy booking (nếu chưa thanh toán)
     */
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = findById(bookingId);
            
        if (booking.getStatus() == BookingStatus.BOOKED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể hủy booking đã thanh toán");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.getSeats().forEach(s -> s.setStatus(SeatStatus.RELEASED));
        
        // Notify seat status changed
        notificationService.notifySeatStatusChanged(booking.getShowtimeId(), booking.getSeats());
    }

    /**
     * Lấy lịch sử booking của user
     */
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Lưu booking
     */
    @Transactional
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }
}