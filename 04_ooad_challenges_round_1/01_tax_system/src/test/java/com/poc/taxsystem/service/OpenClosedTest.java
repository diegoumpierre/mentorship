package com.poc.taxsystem.service;

import com.poc.taxsystem.model.EffectivePeriod;
import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.model.TaxRate;
import com.poc.taxsystem.repository.ProductRepository;
import com.poc.taxsystem.repository.StateRepository;
import com.poc.taxsystem.repository.TaxRateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OpenClosedTest {

    @Autowired
    private TaxEngine engine;

    @Autowired
    private StateRepository states;

    @Autowired
    private ProductRepository products;

    @Autowired
    private TaxRateRepository rates;

    @Test
    @Transactional
    void plugarEstadoNovoSoMexeNaBase() {
        State fl = states.save(new State("FL", "Florida"));
        Product gadget = products.save(new Product("Gadget FL", new BigDecimal("100.00")));
        rates.save(new TaxRate(gadget, fl, new BigDecimal("6.0000"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), null)));

        BigDecimal tax = engine.taxFor(gadget, fl, LocalDate.of(2024, 6, 1));

        assertThat(tax).isEqualByComparingTo("6.00");
    }

    @Test
    @Transactional
    void plugarAnoNovoSoMexeNaBase() {
        Product gadget = products.save(new Product("Year Gadget", new BigDecimal("100.00")));
        State ca = states.findById("CA").orElseThrow();
        rates.save(new TaxRate(gadget, ca, new BigDecimal("5.0000"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1))));
        rates.save(new TaxRate(gadget, ca, new BigDecimal("5.5000"),
                new EffectivePeriod(LocalDate.of(2025, 1, 1), LocalDate.of(2026, 1, 1))));
        rates.save(new TaxRate(gadget, ca, new BigDecimal("6.0000"),
                new EffectivePeriod(LocalDate.of(2026, 1, 1), null)));

        assertThat(engine.taxFor(gadget, ca, LocalDate.of(2024, 6, 1))).isEqualByComparingTo("5.00");
        assertThat(engine.taxFor(gadget, ca, LocalDate.of(2025, 6, 1))).isEqualByComparingTo("5.50");
        assertThat(engine.taxFor(gadget, ca, LocalDate.of(2026, 6, 1))).isEqualByComparingTo("6.00");
    }
}
