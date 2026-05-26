package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.model.TaxRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EffectivePeriodTaxRule implements TaxRule {

    private final TaxRate rate;

    public EffectivePeriodTaxRule(TaxRate rate) {
        this.rate = rate;
    }

    @Override
    public boolean appliesTo(Product product, State state, LocalDate when) {
        return rate.getProduct().getId().equals(product.getId())
                && rate.getState().getCode().equals(state.getCode())
                && rate.getPeriod().contains(when);
    }

    @Override
    public BigDecimal percent() {
        return rate.getPercent();
    }
}
