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

import java.time.Instant;
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

    private Long bookingId;

    @BeforeEach
    void setup() throws Exception {
        // Tạo showtime test
        Showtime showtime = new Showtime();
        showtime.setTheatreId(1L);
        showtime.setMovieId(1L);
        showtime.setStartTime(Instant.now().plusSeconds(86400)); // +1 day
        showtime.setEndTime(Instant.now().plusSeconds(86400 + 7200)); // +1 day +2 hours
        showtime = showtimeRepository.save(showtime);

        // Tạo booking test
        CreateBookingRequest req = new CreateBookingRequest(showtime.getId(), List.of("A1","A2"));
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
