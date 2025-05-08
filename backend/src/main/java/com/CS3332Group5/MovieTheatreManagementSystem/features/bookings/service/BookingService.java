package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.CreateBookingRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.Booking;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingSeat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository.BookingRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.repository.BookingSeatRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.repository.SeatRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.CustomerRepository;
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
    private SeatRepository seatRepository;

    @Autowired
    private BookingNotificationService notificationService;

    @Autowired
    private CustomerRepository customerRepository;

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
        Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Suất chiếu với id=" + showtimeId + " không tồn tại"
            ));
        Customer customer = customerRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"));
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setShowtime(showtime);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(Instant.now());
        // Snapshot movie title và start time
        booking.setMovieTitleSnapshot(showtime.getMovie().getTitle());
        booking.setStartTimeSnapshot(showtime.getStartTime());
        booking = bookingRepository.save(booking);

        // Hold từng ghế (RESERVED)
        for (Long seatId : req.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ghế không tồn tại"));
            // Kiểm double-booking
            boolean exists = bookingSeatRepository.existsByBooking_Showtime_IdAndSeat_IdAndStatusIn(
                showtimeId, seatId,
                List.of(SeatStatus.RESERVED, SeatStatus.BOOKED)
            );
            if (exists) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ghế " + seat.getRowLabel() + seat.getColNumber() + " đã có người giữ/đặt"
                );
            }
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setStatus(SeatStatus.RESERVED);
            bookingSeat.setReservedAt(Instant.now());
            bookingSeat.setPrice(100000); // snapshot giá ghế, có thể thay đổi sau
            booking.getSeats().add(bookingSeat);
        }
        // Notify seat status changed
        notificationService.notifySeatStatusChanged(showtimeId, booking.getSeats());
        return booking;
    }

    /**
     * Toggle chọn/hủy chọn ghế (chỉ khi PENDING)
     */
    @Transactional
    public Booking toggleSeat(Long bookingId, Long seatId) {
        Booking booking = findById(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể chỉnh ghế khi đang PENDING");
        }
        // Nếu đã có trong list thì remove (deselect)
        boolean removed = booking.getSeats().removeIf(s -> s.getSeat() != null && s.getSeat().getId().equals(seatId));
        if (!removed) {
            // Nếu chưa có thì thêm (select) với trạng thái RESERVED
            Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ghế không tồn tại"));
            if (bookingSeatRepository.existsByBooking_Showtime_IdAndSeat_IdAndStatusIn(
                booking.getShowtime().getId(), seatId,
                List.of(SeatStatus.RESERVED, SeatStatus.BOOKED)
            )) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ghế đã có người giữ/đặt");
            }
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setStatus(SeatStatus.RESERVED);
            bookingSeat.setReservedAt(Instant.now());
            bookingSeat.setPrice(100000); // snapshot giá ghế, có thể thay đổi sau
            booking.getSeats().add(bookingSeat);
        }
        // Notify seat status changed
        notificationService.notifySeatStatusChanged(booking.getShowtime().getId(), booking.getSeats());
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
            notificationService.notifySeatStatusChanged(b.getShowtime().getId(), b.getSeats());
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
        notificationService.notifySeatStatusChanged(booking.getShowtime().getId(), booking.getSeats());
    }

    /**
     * Lấy lịch sử booking của user
     */
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long userId) {
        Customer customer = customerRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"));
        return bookingRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    /**
     * Lưu booking
     */
    @Transactional
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    /**
     * Tính tổng tiền booking dựa trên số ghế (giá cố định 100000L mỗi ghế)
     */
    public Long calculateTotalAmount(Booking booking) {
        return booking.getSeats().stream()
            .mapToLong(BookingSeat::getPrice)
            .sum();
    }
}