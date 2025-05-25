package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.BookingDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.BookingSeatDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.BookingMapper;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.CreateBookingRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.SeatBatchActionRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.Booking;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingSeat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service.BookingService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service.VNPayService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final VNPayService vnPayService;
    @Autowired
    private CustomerRepository customerRepository;
    
    public BookingController(BookingService bookingService, VNPayService vnPayService, CustomerRepository customerRepository) {
        this.bookingService = bookingService;
        this.vnPayService = vnPayService;
        this.customerRepository = customerRepository;
    }

    // 1. Start booking
    @PostMapping
    public ResponseEntity<BookingDto> create(
        @Valid @RequestBody CreateBookingRequest req,
        java.security.Principal principal
    ) {
        Long userId = customerRepository.findByUsername(principal.getName())
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại")).getId();
        Booking booking = bookingService.startBooking(req, userId);
        return ResponseEntity.ok(bookingService.toDtoWithNames(booking));
    }

    // 2. Select/Deselect seat (dùng seatId thay vì seatCode)
    @PatchMapping("/{id}/seat/{seatId}")
    public ResponseEntity<BookingDto> toggleSeat(
        @PathVariable Long id,
        @PathVariable Long seatId,
        java.security.Principal principal
    ) {
        Long userId = customerRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"))
            .getId();
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        Booking updated = bookingService.toggleSeat(id, seatId);
        return ResponseEntity.ok(bookingService.toDtoWithNames(updated));
    }

    // 2. Select/Deselect multiple seats (batch)
    @PatchMapping("/{id}/seats")
    public ResponseEntity<BookingDto> toggleSeats(
        @PathVariable Long id,
        @RequestBody SeatBatchActionRequest req,
        java.security.Principal principal
    ) {
        Long userId = customerRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"))
            .getId();
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        Booking updated = bookingService.toggleSeats(id, req.getSeatIds());
        return ResponseEntity.ok(bookingService.toDtoWithNames(updated));
    }

    // 3. Confirm booking
    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingDto> confirm(
        @PathVariable Long id,
        java.security.Principal principal
    ) {
        Long userId = customerRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"))
            .getId();
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        Booking updated = bookingService.confirm(id);
        return ResponseEntity.ok(bookingService.toDtoWithNames(updated));
    }

    // 4. Fetch seat statuses for a showtime
    @GetMapping("/showtime/{showtimeId}/seats")
    public ResponseEntity<List<BookingSeatDto>> getSeatStatuses(@PathVariable Long showtimeId) {
        List<BookingSeat> seats = bookingService.getSeatStatuses(showtimeId);
        List<BookingSeatDto> dtos = seats.stream().map(BookingMapper::toSeatDto).toList();
        return ResponseEntity.ok(dtos);
    }

    // 5. Cancel booking
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
        @PathVariable Long id,
        java.security.Principal principal
    ) {
        Long userId = customerRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"))
            .getId();
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }

    // 6. Get user's booking history
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDto>> getMyBookings(java.security.Principal principal) {
        Long userId = customerRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"))
            .getId();
        List<Booking> bookings = bookingService.getUserBookings(userId);
        // Only return bookings that are BOOKED or not-yet-expired AWAITING_PAYMENT
        List<BookingDto> filtered = bookingService.toDtoListWithNames(
            bookings.stream().filter(b ->
                b.getStatus() == BookingStatus.BOOKED ||
                (b.getStatus() == BookingStatus.AWAITING_PAYMENT &&
                 b.getBookingDate().isAfter(java.time.Instant.now().minus(BookingService.HOLD_DURATION))
                )
            ).toList()
        );
        return ResponseEntity.ok(filtered);
    }

    // 7. Create payment URL
    @PostMapping("/{id}/pay")
    public ResponseEntity<String> createPayment(
        @PathVariable Long id,
        java.security.Principal principal,
        HttpServletRequest request
    ) {
        Long userId = customerRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"))
            .getId();
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        Long amount = bookingService.calculateTotalAmount(booking);
        String orderInfo = "Thanh toán vé xem phim - BookingID: " + id;
        String paymentUrl = vnPayService.createPaymentUrl(id, amount, orderInfo, request);
        return ResponseEntity.ok(paymentUrl);
    }

    // 8. VNPay return handler
    @GetMapping("/vnpay-return")
    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> queryParams) {
        if (vnPayService.validatePaymentResponse(queryParams)) {
            String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
            String vnp_TxnRef = queryParams.get("vnp_TxnRef");
            if ("00".equals(vnp_ResponseCode)) {
                // Thanh toán thành công
                Long bookingId = Long.parseLong(vnp_TxnRef);
                Booking booking = bookingService.findById(bookingId);
                booking.setStatus(BookingStatus.BOOKED);
                booking.getSeats().forEach(s -> s.setStatus(SeatStatus.BOOKED));
                bookingService.save(booking);
                return ResponseEntity.ok("Thanh toán thành công");
            } else {
                return ResponseEntity.ok("Thanh toán thất bại");
            }
        }
        return ResponseEntity.badRequest().body("Invalid payment response");
    }

    /**
     * Get the current user's PENDING booking for a showtime (for seat selection page reload)
     */
    @GetMapping("/current")
    public ResponseEntity<BookingDto> getCurrentBooking(
        @RequestParam Long showtimeId,
        Principal principal
    ) {
        Long userId = customerRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"))
            .getId();
        Booking booking = bookingService.getCurrentActiveBooking(showtimeId, userId);
        if (booking == null) {
            booking = bookingService.getOrCreatePendingBooking(showtimeId, userId);
        }
        return ResponseEntity.ok(bookingService.toDtoWithNames(booking));
    }
}