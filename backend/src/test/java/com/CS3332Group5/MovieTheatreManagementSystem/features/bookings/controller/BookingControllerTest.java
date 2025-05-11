package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.controller;

import com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.dto.CreateBookingRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.repository.ShowtimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "1", roles = {"CUSTOMER"})
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private com.CS3332Group5.MovieTheatreManagementSystem.features.seats.repository.SeatRepository seatRepository;

    @Autowired
    private com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository.ScreenRepository screenRepository;

    private Long bookingId;

    @BeforeEach
    void setup() throws Exception {
        // Tạo screen test
        var screen = new com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Screen();
        screen.setName("TestScreen");
        screen.setCapacity(20);
        screen = screenRepository.save(screen);

        // Tạo 2 ghế test
        var seat1 = new com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat();
        seat1.setRowLabel("A");
        seat1.setColNumber(1);
        seat1.setScreen(screen);
        seat1 = seatRepository.save(seat1);
        var seat2 = new com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat();
        seat2.setRowLabel("A");
        seat2.setColNumber(2);
        seat2.setScreen(screen);
        seat2 = seatRepository.save(seat2);

        // Tạo showtime test
        Showtime showtime = new Showtime();
        showtime.setScreen(screen);
        showtime.setStartTime(OffsetDateTime.now().plusDays(1)); // +1 day
        showtime.setEndTime(OffsetDateTime.now().plusDays(1).plusHours(2)); // +1 day +2 hours
        showtime = showtimeRepository.save(showtime);

        // Tạo booking test với ID ghế
        CreateBookingRequest req = new CreateBookingRequest(showtime.getId(), List.of(seat1.getId(), seat2.getId()));
        String json = objectMapper.writeValueAsString(req);

        String response = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        bookingId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void testToggleSeat() throws Exception {
        mockMvc.perform(patch("/api/bookings/{id}/seat/{seat}", bookingId, "A1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.seats").isArray());
    }

    @Test
    void testConfirmBooking() throws Exception {
        mockMvc.perform(post("/api/bookings/{id}/confirm", bookingId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("AWAITING_PAYMENT"));
    }
}
