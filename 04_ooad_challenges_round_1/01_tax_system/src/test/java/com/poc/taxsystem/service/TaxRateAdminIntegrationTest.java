package com.poc.taxsystem.service;

import com.poc.taxsystem.model.EffectivePeriod;
import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.model.TaxRate;
import com.poc.taxsystem.repository.ProductRepository;
import com.poc.taxsystem.repository.StateRepository;
import com.poc.taxsystem.repository.TaxRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaxRateAdminIntegrationTest {

    @Autowired
    private TaxRateAdmin admin;

    @Autowired
    private CachingTaxRuleProvider cache;

    @Autowired
    private TaxEngine engine;

    @Autowired
    private ProductRepository products;

    @Autowired
    private StateRepository states;

    @Autowired
    private TaxRateRepository rates;

    @BeforeEach
    void clearCache() {
        cache.invalidateAll();
    }

    @Test
    @Transactional
    void admincacheLimpaAposNovoRate() {
        Product gadget = products.save(new Product("CacheGadget", new BigDecimal("100.00")));
        State ca = states.findById("CA").orElseThrow();
        admin.add(new TaxRate(gadget, ca, new BigDecimal("5.0000"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), null)));

        assertThat(engine.taxFor(gadget, ca, LocalDate.of(2024, 6, 1)))
                .isEqualByComparingTo("5.00");

        rates.deleteAll(rates.findByProductIdAndStateCode(gadget.getId(), "CA"));
        rates.flush();
        admin.add(new TaxRate(gadget, ca, new BigDecimal("8.0000"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), null)));

        assertThat(engine.taxFor(gadget, ca, LocalDate.of(2024, 6, 1)))
                .isEqualByComparingTo("8.00");
    }
}
