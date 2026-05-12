package com.poc.taxsystem.repository;

import com.poc.taxsystem.model.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {

    List<TaxRate> findByProductIdAndStateCode(Long productId, String stateCode);
}
