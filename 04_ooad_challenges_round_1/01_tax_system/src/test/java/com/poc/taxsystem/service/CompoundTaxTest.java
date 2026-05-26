package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.repository.ProductRepository;
import com.poc.taxsystem.repository.StateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompoundTaxTest {

    @Autowired
    private TaxEngine engine;

    @Autowired
    private FederalTaxRuleProvider federal;

    @Autowired
    private ProductRepository products;

    @Autowired
    private StateRepository states;

    @AfterEach
    void clearFederal() {
        federal.clear();
    }

    @Test
    void federalSomaPorCimaDoState() {
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        federal.set(laptop.getId(), new BigDecimal("3.0000"));

        BigDecimal tax = engine.taxFor(laptop, ca, LocalDate.of(2024, 6, 1));

        // state CA 7.25 + federal 3.0 = 10.25 → 1500 * 10.25 / 100 = 153.75
        assertThat(tax).isEqualByComparingTo("153.75");
    }

    @Test
    void semFederalEngineContinuaSoCobrandoState() {
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();

        BigDecimal tax = engine.taxFor(laptop, ca, LocalDate.of(2024, 6, 1));

        assertThat(tax).isEqualByComparingTo("108.75");
    }

    @Test
    void produtoSemStateRateMasComFederalCobraSoOFederal() {
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        federal.set(laptop.getId(), new BigDecimal("3.0000"));

        // date before any CA period → no state rule, but federal applies
        BigDecimal tax = engine.taxFor(laptop, ca, LocalDate.of(2020, 1, 1));

        // 1500 * 3.0 / 100 = 45.00
        assertThat(tax).isEqualByComparingTo("45.00");
    }
}
