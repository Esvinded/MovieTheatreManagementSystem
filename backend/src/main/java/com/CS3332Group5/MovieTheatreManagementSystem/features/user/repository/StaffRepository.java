package com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUsername(String username);
    Optional<Staff> findByEmail(String email);
}