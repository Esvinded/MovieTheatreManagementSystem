package com.CS3332Group5.MovieTheatreManagementSystem.features.user.repository;

import com.CS3332Group5.MovieTheatreManagementSystem.features.user.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByEmail(String email);
}