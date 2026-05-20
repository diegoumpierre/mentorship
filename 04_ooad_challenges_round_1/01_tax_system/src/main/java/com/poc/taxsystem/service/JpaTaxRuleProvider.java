package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.repository.TaxRateRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class JpaTaxRuleProvider implements TaxRuleProvider {

    private final TaxRateRepository rates;

    public JpaTaxRuleProvider(TaxRateRepository rates) {
        this.rates = rates;
    }

    @Override
    public List<TaxRule> rulesFor(Product product, State state, LocalDate when) {
        return rates.findByProductIdAndStateCode(product.getId(), state.getCode())
                .stream()
                .<TaxRule>map(EffectivePeriodTaxRule::new)
                .filter(r -> r.appliesTo(product, state, when))
                .toList();
    }
}
