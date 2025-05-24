package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service.BookingService;

@Controller
public class SeatSelectionWebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private BookingService bookingService;

    @MessageMapping("/seat/select")
    public void selectSeat(SeatSelectionMessage msg, Principal principal) {
        bookingService.lockSeat(msg.getShowtimeId(), msg.getSeatId(), principal.getName());
        messagingTemplate.convertAndSend(
            "/topic/showtime/" + msg.getShowtimeId() + "/seats",
            bookingService.getSeatStatuses(msg.getShowtimeId())
        );
    }

    @MessageMapping("/seat/deselect")
    public void deselectSeat(SeatSelectionMessage msg, Principal principal) {
        bookingService.unlockSeat(msg.getShowtimeId(), msg.getSeatId(), principal.getName());
        messagingTemplate.convertAndSend(
            "/topic/showtime/" + msg.getShowtimeId() + "/seats",
            bookingService.getSeatStatuses(msg.getShowtimeId())
        );
    }
}
