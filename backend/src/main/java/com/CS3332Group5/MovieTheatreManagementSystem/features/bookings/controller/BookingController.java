package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.CreateBookingRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.Booking;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingSeat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.SeatStatus;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service.BookingService;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service.VNPayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final VNPayService vnPayService;
    
    public BookingController(BookingService bookingService, VNPayService vnPayService) {
        this.bookingService = bookingService;
        this.vnPayService = vnPayService;
    }

    // 1. Start booking
    @PostMapping
    public ResponseEntity<Booking> create(
        @Valid @RequestBody CreateBookingRequest req,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(bookingService.startBooking(req, userId));
    }

    // 2. Select/Deselect seat
    @PatchMapping("/{id}/seat/{seatCode}")
    public ResponseEntity<Booking> toggleSeat(
        @PathVariable Long id,
        @PathVariable String seatCode,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        return ResponseEntity.ok(bookingService.toggleSeat(id, seatCode));
    }

    // 3. Confirm booking
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirm(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getUserId().equals(userId)) {
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
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }

    // 6. Get user's booking history
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    // 7. Create payment URL
    @PostMapping("/{id}/pay")
    public ResponseEntity<String> createPayment(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Booking booking = bookingService.findById(id);
        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền thao tác booking này");
        }
        
        // TODO: Tính toán số tiền thực tế dựa trên số ghế và giá vé
        Long amount = 100000L; // VND
        String orderInfo = "Thanh toan ve xem phim - BookingID: " + id;
        
        String paymentUrl = vnPayService.createPaymentUrl(id, amount, orderInfo);
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