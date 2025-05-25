package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.BookingDto;
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
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity.Movie;
import com.CS3332Group5.MovieTheatreManagementSystem.features.movies.repository.MovieRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity.Screen;
import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.repository.ScreenRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.TheatreRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.BookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

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

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;
    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private BookingService bookingServiceProxy;

    public static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    // Helper: statuses for active bookings
    private static final java.util.List<BookingStatus> ACTIVE_BOOKING_STATUSES = java.util.List.of(
        BookingStatus.PENDING, BookingStatus.AWAITING_PAYMENT, BookingStatus.BOOKED
    );
    private static final java.util.List<SeatStatus> ACTIVE_SEAT_STATUSES = java.util.List.of(
        SeatStatus.RESERVED, SeatStatus.BOOKED
    );

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
        booking.setBookingDate(Instant.now());
        // Snapshot movie title và start time
        String movieTitle = movieRepository.findById(showtime.getMovieId())
            .map(Movie::getTitle)
            .orElse("Unknown");
        booking.setMovieTitleSnapshot(movieTitle);
        booking.setStartTimeSnapshot(showtime.getStartTime());
        booking = bookingRepository.save(booking);

        // Hold từng ghế (RESERVED)
        for (Long seatId : req.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ghế không tồn tại"));
            // Kiểm double-booking (chỉ tính booking còn hiệu lực)
            boolean exists = bookingSeatRepository.existsActiveSeat(
                showtimeId, seatId,
                ACTIVE_SEAT_STATUSES,
                ACTIVE_BOOKING_STATUSES
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
            bookingSeat.setPrice(calculateSeatPrice(seat)); // snapshot giá ghế, có thể thay đổi sau
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
            // Check ghế đã bị giữ/đặt chưa
            boolean isTaken = bookingSeatRepository.existsActiveSeat(
                booking.getShowtime().getId(), seatId,
                ACTIVE_SEAT_STATUSES,
                ACTIVE_BOOKING_STATUSES
            );
            if (isTaken) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ghế đã có người giữ/đặt");
            }
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setStatus(SeatStatus.RESERVED);
            bookingSeat.setReservedAt(Instant.now());
            bookingSeat.setPrice(calculateSeatPrice(seat)); // snapshot giá ghế, có thể thay đổi sau
            booking.getSeats().add(bookingSeat);
        }
        // Notify seat status changed
        notificationService.notifySeatStatusChanged(booking.getShowtime().getId(), booking.getSeats());
        return booking;
    }

    /**
     * Toggle chọn/hủy chọn nhiều ghế (chỉ khi PENDING)
     */
    @Transactional
    public Booking toggleSeats(Long bookingId, List<Long> seatIds) {
        Booking booking = findById(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được chọn/hủy ghế khi booking đang PENDING");
        }
        // Logic: Nếu seatId đã có thì hủy, chưa có thì thêm
        for (Long seatId : seatIds) {
            boolean exists = booking.getSeats().stream().anyMatch(bs -> bs.getSeat().getId().equals(seatId));
            if (exists) {
                booking.removeSeat(seatId);
            } else {
                Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ghế không tồn tại"));
                // Check ghế đã bị giữ/đặt chưa
                boolean isTaken = bookingSeatRepository.existsActiveSeat(
                    booking.getShowtime().getId(), seatId,
                    ACTIVE_SEAT_STATUSES,
                    ACTIVE_BOOKING_STATUSES
                );
                if (isTaken) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ghế đã được giữ hoặc đặt");
                }
                BookingSeat bs = new BookingSeat(seat);
                bs.setStatus(SeatStatus.RESERVED);
                // Nếu Seat chưa có trường price, dùng giá mặc định hoặc sửa lại entity Seat để có getPrice()
                bs.setPrice(calculateSeatPrice(seat)); // hoặc giá mặc định khác nếu cần
                booking.addSeat(bs);
            }
        }
        bookingRepository.save(booking);
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
            ACTIVE_SEAT_STATUSES
        );
    }

    /**
     * Scheduler: release các booking PENDING quá HOLD_DURATION
     */
    @Transactional
    public void releaseExpired() {
        Instant cutoff = Instant.now().minus(HOLD_DURATION);
        List<Booking> olds = bookingRepository.findByStatusAndBookingDateBefore(BookingStatus.PENDING, cutoff);
        logger.info("[releaseExpired] Found {} expired PENDING bookings", olds.size());
        for (Booking b : olds) {
            if (b.getStatus() != BookingStatus.PENDING) continue;
            b.setStatus(BookingStatus.EXPIRED);
            for (BookingSeat seat : b.getSeats()) {
                seat.setStatus(SeatStatus.RELEASED);
                bookingSeatRepository.save(seat);
            }
            bookingRepository.save(b);
            logger.info("[releaseExpired] Expired booking id={} (timeout)", b.getId());
            notificationService.notifyBookingExpired(b.getId());
            notificationService.notifySeatStatusChanged(b.getShowtime().getId(), b.getSeats());
        }
    }

    /**
     * Scheduler: release các booking AWAITING_PAYMENT quá HOLD_DURATION
     */
    @Transactional
    public void releaseExpiredAwaitingPayment() {
        Instant cutoff = Instant.now().minus(HOLD_DURATION);
        List<Booking> olds = bookingRepository.findByStatusAndBookingDateBefore(BookingStatus.AWAITING_PAYMENT, cutoff);
        logger.info("[releaseExpiredAwaitingPayment] Found {} expired AWAITING_PAYMENT bookings", olds.size());
        for (Booking b : olds) {
            if (b.getStatus() != BookingStatus.AWAITING_PAYMENT) continue;
            b.setStatus(BookingStatus.EXPIRED);
            for (BookingSeat seat : b.getSeats()) {
                seat.setStatus(SeatStatus.RELEASED);
                bookingSeatRepository.save(seat);
            }
            bookingRepository.save(b);
            logger.info("[releaseExpiredAwaitingPayment] Expired booking id={} (timeout)", b.getId());
            notificationService.notifyBookingExpired(b.getId());
            notificationService.notifySeatStatusChanged(b.getShowtime().getId(), b.getSeats());
        }
    }

    /**
     * Hủy booking (nếu chưa thanh toán)
     */
    @Transactional
    public void cancelBooking(Long bookingId) {
        logger.info("[cancelBooking] Cancelling booking id={}", bookingId);
        Booking booking = bookingServiceProxy.findById(bookingId);
        if (booking.getStatus() == BookingStatus.BOOKED) {
            logger.warn("[cancelBooking] Attempted to cancel already BOOKED booking id={}", bookingId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể hủy booking đã thanh toán");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.getSeats().forEach(seat -> {
            seat.setStatus(SeatStatus.RELEASED);
            bookingSeatRepository.save(seat);
        });
        bookingRepository.save(booking);
        notificationService.notifySeatStatusChanged(booking.getShowtime().getId(), booking.getSeats());
        logger.info("[cancelBooking] Booking id={} set to CANCELLED and seats released", bookingId);
    }

    /**
     * Lấy lịch sử booking của user
     */
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long userId) {
        Customer customer = customerRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"));
        return bookingRepository.findByCustomerOrderByBookingDateDesc(customer);
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

    /**
     * Tính giá vé dựa trên vị trí ghế (row, colNumber)
     */
    private int calculateSeatPrice(Seat seat) {
        String row = seat.getRowLabel();
        int col = seat.getColNumber();
        // Ví dụ: Hàng D, E, F và ghế giữa (col 5-8) là VIP
        if (("D".equalsIgnoreCase(row) || "E".equalsIgnoreCase(row) || "F".equalsIgnoreCase(row)) && col >= 5 && col <= 8) {
            return 150000; // VIP
        } else if (col == 1 || col == 10) {
            return 80000; // Rìa
        } else {
            return 100000; // Thường
        }
    }

    /**
     * Clean up booking seats after a showtime ends: release seats for EXPIRED or CANCELLED bookings.
     * BOOKED bookings are kept for history.
     */
    @Transactional
    public void cleanUpBookingSeatsAfterShowtime(Long showtimeId) {
        List<Booking> expiredBookings = bookingRepository.findByShowtimeIdInAndStatus(
            List.of(showtimeId), BookingStatus.EXPIRED
        );
        List<Booking> cancelledBookings = bookingRepository.findByShowtimeIdInAndStatus(
            List.of(showtimeId), BookingStatus.CANCELLED
        );
        for (Booking booking : expiredBookings) {
            for (BookingSeat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.RELEASED);
                bookingSeatRepository.save(seat); // Explicitly persist seat status
            }
            notificationService.notifySeatStatusChanged(showtimeId, booking.getSeats());
        }
        for (Booking booking : cancelledBookings) {
            for (BookingSeat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.RELEASED);
                bookingSeatRepository.save(seat); // Explicitly persist seat status
            }
            notificationService.notifySeatStatusChanged(showtimeId, booking.getSeats());
        }
    }

    public BookingDto toDtoWithNames(Booking booking) {
        // Use BookingMapper.toDto directly, as it already contains the logic
        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> toDtoListWithNames(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    /**
     * Get or create a PENDING booking for a customer and showtime (not expired), using repository for atomicity
     */
    @Transactional
    public Booking getOrCreatePendingBooking(Long showtimeId, Long customerId) {
        Instant cutoff = Instant.now().minus(HOLD_DURATION);
        // Use repository for efficient lookup
        Booking booking = bookingRepository.findByCustomerIdAndShowtimeIdAndStatus(customerId, showtimeId, BookingStatus.PENDING)
            .filter(b -> b.getBookingDate().isAfter(cutoff))
            .orElse(null);
        if (booking == null) {
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"));
            Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Suất chiếu không tồn tại"));
            booking = new Booking();
            booking.setCustomer(customer);
            booking.setShowtime(showtime);
            booking.setStatus(BookingStatus.PENDING);
            booking.setBookingDate(Instant.now());
            String movieTitle = movieRepository.findById(showtime.getMovieId())
                .map(Movie::getTitle)
                .orElse("Unknown");
            booking.setMovieTitleSnapshot(movieTitle);
            booking.setStartTimeSnapshot(showtime.getStartTime());
            booking = bookingRepository.save(booking);
        }
        return booking;
    }

    /**
     * Get the current active booking (PENDING, not expired) for a customer and showtime
     */
    @Transactional(readOnly = true)
    public Booking getCurrentActiveBooking(Long showtimeId, Long customerId) {
        Instant cutoff = Instant.now().minus(HOLD_DURATION);
        return bookingRepository.findAll().stream()
            .filter(b -> b.getCustomer().getId().equals(customerId)
                && b.getShowtime().getId().equals(showtimeId)
                && b.getStatus() == BookingStatus.PENDING
                && b.getBookingDate().isAfter(cutoff))
            .findFirst()
            .orElse(null);
    }

    /**
     * Real-time seat lock: only use the current user's PENDING booking for this showtime
     */
    @Transactional
    public void lockSeat(Long showtimeId, Long seatId, String username) {
        Customer customer = customerRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"));
        Booking booking = bookingServiceProxy.getOrCreatePendingBooking(showtimeId, customer.getId());
        // Check if seat is already reserved/booked by any active booking (other than this booking)
        boolean isTaken = bookingSeatRepository.existsActiveSeat(
            showtimeId, seatId,
            ACTIVE_SEAT_STATUSES,
            ACTIVE_BOOKING_STATUSES
        );
        boolean alreadyInBooking = booking.getSeats().stream().anyMatch(bs -> bs.getSeat().getId().equals(seatId));
        if (isTaken && !alreadyInBooking) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ghế đã có người giữ/đặt");
        }
        if (!alreadyInBooking) {
            Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ghế không tồn tại"));
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setStatus(SeatStatus.RESERVED);
            bookingSeat.setReservedAt(Instant.now());
            bookingSeat.setPrice(calculateSeatPrice(seat));
            booking.getSeats().add(bookingSeat);
            bookingRepository.save(booking);
        }
    }

    /**
     * Real-time seat unlock: only use the current user's PENDING booking for this showtime
     */
    @Transactional
    public void unlockSeat(Long showtimeId, Long seatId, String username) {
        Customer customer = customerRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"));
        Instant cutoff = Instant.now().minus(HOLD_DURATION);
        Booking booking = bookingRepository.findAll().stream()
            .filter(b -> b.getCustomer().getId().equals(customer.getId())
                && b.getShowtime().getId().equals(showtimeId)
                && b.getStatus() == BookingStatus.PENDING
                && b.getBookingDate().isAfter(cutoff))
            .findFirst()
            .orElse(null);
        if (booking == null) return;
        boolean removed = booking.getSeats().removeIf(bs -> bs.getSeat().getId().equals(seatId));
        if (removed) {
            bookingRepository.save(booking);
        }
    }
}