package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FederalTaxRuleProvider implements CompoundTaxLayer {

    private final Map<Long, BigDecimal> percentByProductId = new ConcurrentHashMap<>();

    @Override
    public List<TaxRule> rulesFor(Product product, State state, LocalDate when) {
        BigDecimal percent = percentByProductId.get(product.getId());
        if (percent == null) {
            return List.of();
        }
        return List.of(new FlatPercentTaxRule(percent));
    }

    public void set(Long productId, BigDecimal percent) {
        percentByProductId.put(productId, percent);
    }

    public void clear() {
        percentByProductId.clear();
    }
}
