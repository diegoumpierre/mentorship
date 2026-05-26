package com.poc.taxsystem.service;

import com.poc.taxsystem.model.TaxRate;
import com.poc.taxsystem.repository.TaxRateRepository;
import org.springframework.stereotype.Service;

@Service
public class TaxRateAdmin {

    private final TaxRateRepository rates;
    private final CachingTaxRuleProvider cache;

    public TaxRateAdmin(TaxRateRepository rates, CachingTaxRuleProvider cache) {
        this.rates = rates;
        this.cache = cache;
    }

    public TaxRate add(TaxRate rate) {
        TaxRate saved = rates.save(rate);
        cache.invalidate(saved.getProduct().getId(), saved.getState().getCode());
        return saved;
    }
}
