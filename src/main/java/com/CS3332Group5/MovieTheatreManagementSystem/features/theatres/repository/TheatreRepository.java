package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
}
