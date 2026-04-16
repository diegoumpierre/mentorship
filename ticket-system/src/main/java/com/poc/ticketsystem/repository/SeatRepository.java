package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
