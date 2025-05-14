package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    /** Lấy tất cả seat theo screenId */
    List<Seat> findByScreenId(Long screenId);
}
