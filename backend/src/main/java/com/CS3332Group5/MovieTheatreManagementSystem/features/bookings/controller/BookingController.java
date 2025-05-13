package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.controller;

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
import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity<Booking> create(
        @Valid @RequestBody CreateBookingRequest req,
        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(bookingService.startBooking(req, userId));
    }

    // 2. Select/Deselect seat (dùng seatId thay vì seatCode)
    @PatchMapping("/{id}/seat/{seatId}")
    public ResponseEntity<Booking> toggleSeat(
        @PathVariable Long id,
        @PathVariable Long seatId,
        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        return ResponseEntity.ok(bookingService.toggleSeat(id, seatId));
    }

    // 2. Select/Deselect multiple seats (batch)
    @PatchMapping("/{id}/seats")
    public ResponseEntity<Booking> toggleSeats(
        @PathVariable Long id,
        @RequestBody SeatBatchActionRequest req,
        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        return ResponseEntity.ok(bookingService.toggleSeats(id, req.getSeatIds()));
    }

    // 3. Confirm booking
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirm(
        @PathVariable Long id,
        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        return ResponseEntity.ok(bookingService.confirm(id));
    }

    // 4. Fetch seat statuses for a showtime
    @GetMapping("/showtime/{showtimeId}/seats")
    public ResponseEntity<List<BookingSeat>> getSeatStatuses(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(bookingService.getSeatStatuses(showtimeId));
    }

    // 5. Cancel booking
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
        @PathVariable Long id,
        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }

    // 6. Get user's booking history
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Customer customer = customerRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer không tồn tại"));
        return ResponseEntity.ok(bookingService.getUserBookings(customer.getId()));
    }

    // 7. Create payment URL
    @PostMapping("/{id}/pay")
    public ResponseEntity<String> createPayment(
        @PathVariable Long id,
        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
        HttpServletRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        Long amount = bookingService.calculateTotalAmount(booking);
        String orderInfo = "Thanh toán vé xem phim - BookingID: " + id;
        // Gọi method với request để lấy IP và build returnUrl
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
}