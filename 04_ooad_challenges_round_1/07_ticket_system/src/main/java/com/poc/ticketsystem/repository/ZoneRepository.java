package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}
