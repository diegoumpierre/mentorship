package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TaxRule {

    boolean appliesTo(Product product, State state, LocalDate when);

    BigDecimal percent();
}
