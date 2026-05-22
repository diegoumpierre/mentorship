package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Primary
public class CachingTaxRuleProvider implements TaxRuleProvider {

    private final TaxRuleProvider delegate;
    private final ConcurrentMap<Key, List<TaxRule>> cache = new ConcurrentHashMap<>();

    public CachingTaxRuleProvider(@Qualifier("jpaTaxRuleProvider") TaxRuleProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<TaxRule> rulesFor(Product product, State state, LocalDate when) {
        Key key = new Key(product.getId(), state.getCode(), when);
        return cache.computeIfAbsent(key, k -> delegate.rulesFor(product, state, when));
    }

    public void invalidate(Long productId, String stateCode) {
        cache.keySet().removeIf(k -> Objects.equals(k.productId, productId)
                && Objects.equals(k.stateCode, stateCode));
    }

    public void invalidateAll() {
        cache.clear();
    }

    private record Key(Long productId, String stateCode, LocalDate when) {
    }
}
