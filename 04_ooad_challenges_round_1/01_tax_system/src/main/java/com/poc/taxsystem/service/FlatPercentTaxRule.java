package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FlatPercentTaxRule implements TaxRule {

    private final BigDecimal percent;

    public FlatPercentTaxRule(BigDecimal percent) {
        this.percent = percent;
    }

    @Override
    public boolean appliesTo(Product product, State state, LocalDate when) {
        return true;
    }

    @Override
    public BigDecimal percent() {
        return percent;
    }
}
