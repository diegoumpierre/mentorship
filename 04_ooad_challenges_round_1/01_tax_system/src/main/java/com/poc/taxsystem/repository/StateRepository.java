package com.poc.taxsystem.repository;

import com.poc.taxsystem.model.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, String> {
}
