package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity.BookingSeat;
import java.util.List;

@Service
public class BookingNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public BookingNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * Gửi update về trạng thái ghế cho một suất chiếu
     */
    public void notifySeatStatusChanged(Long showtimeId, List<BookingSeat> seats) {
        messagingTemplate.convertAndSend(
            "/topic/showtime/" + showtimeId + "/seats",
            seats
        );
    }
    
    /**
     * Thông báo booking timeout
     */
    public void notifyBookingExpired(Long bookingId) {
        messagingTemplate.convertAndSend(
            "/topic/booking/" + bookingId + "/expired",
            "Booking đã hết hạn"
        );
    }
} 