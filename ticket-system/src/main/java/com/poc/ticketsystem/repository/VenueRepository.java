package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
