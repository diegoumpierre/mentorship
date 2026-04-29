package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    long countByShowDateIdAndSoldTrue(Long showDateId);

    List<Seat> findBySoldFalseAndReservedUntilBefore(LocalDateTime cutoff);

    List<Seat> findByShowDateIdOrderByIdAsc(Long showDateId);
}
