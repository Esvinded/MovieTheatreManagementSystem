package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository;
import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
public interface ScreenRepository extends JpaRepository<Screen,Long>{
    boolean existsByTheatre_IdAndStatus(Long theatreId, Status status);
}