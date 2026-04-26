package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.ShowDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowDateRepository extends JpaRepository<ShowDate, Long> {

    List<ShowDate> findByShowId(Long showId);
}
