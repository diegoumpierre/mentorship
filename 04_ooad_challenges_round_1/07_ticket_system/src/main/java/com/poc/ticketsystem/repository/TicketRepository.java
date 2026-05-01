package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOrderId(Long orderId);
}
