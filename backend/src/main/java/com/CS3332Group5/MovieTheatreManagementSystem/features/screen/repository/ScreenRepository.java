package com.CS3332Group5.MovieTheatreManagementSystem.features.screen.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.screen.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
    List<Screen> findByTheatreId(Long theatreId);
}
