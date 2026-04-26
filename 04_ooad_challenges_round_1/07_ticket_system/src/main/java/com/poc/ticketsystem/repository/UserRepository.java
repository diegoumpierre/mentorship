package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
