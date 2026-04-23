package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
