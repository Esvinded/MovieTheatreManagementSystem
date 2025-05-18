package com.CS3332Group5.MovieTheatreManagementSystem.features.seats.service;

import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.SeatDto;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.dto.SeatUpdateRequest;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.entity.Seat;
import com.CS3332Group5.MovieTheatreManagementSystem.features.seats.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SeatService {

    private final SeatRepository repo;

    public SeatService(SeatRepository repo) {
        this.repo = repo;
    }

    /** Lấy toàn bộ seat */
    public List<SeatDto> listAll() {
        return repo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /** Lấy chi tiết một seat */
    public SeatDto getById(Long id) {
        Seat s = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Seat not found: " + id));
        return toDto(s);
    }

    /** Lấy tất cả seat theo screen */
    public List<SeatDto> listByScreen(Long screenId) {
        return repo.findByScreenId(screenId).stream()
                .map(this::toDto)
                .toList();
    }

    /** Cập nhật duy nhất status của seat */
    @Transactional
    public SeatDto updateStatus(Long id, SeatUpdateRequest r) {
        Seat s = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Seat not found: " + id));
        s.setStatus(r.getStatus());
        return toDto(repo.save(s));
    }

    /** Xóa seat (nếu cần) */
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private SeatDto toDto(Seat s) {
        return new SeatDto(
                s.getId(),
                s.getRowLabel(),
                s.getColNumber(),
                s.getSeatNumber(),
                s.getStatus(),
                s.getScreen().getId()
        );
    }
}
