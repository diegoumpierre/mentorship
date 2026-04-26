package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}
