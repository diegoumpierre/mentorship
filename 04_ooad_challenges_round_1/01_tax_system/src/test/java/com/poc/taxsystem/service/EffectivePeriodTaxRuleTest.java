package com.poc.taxsystem.service;

import com.poc.taxsystem.model.EffectivePeriod;
import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.model.TaxRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class EffectivePeriodTaxRuleTest {

    @Test
    void aplicaQuandoProductStateEDataBatemComOPeriodo() throws Exception {
        Product laptop = product(1L);
        State ca = state("CA");
        TaxRate rate = new TaxRate(laptop, ca, new BigDecimal("7.25"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1)));

        TaxRule rule = new EffectivePeriodTaxRule(rate);

        assertThat(rule.appliesTo(laptop, ca, LocalDate.of(2024, 6, 1))).isTrue();
        assertThat(rule.percent()).isEqualByComparingTo("7.25");
    }

    @Test
    void naoAplicaQuandoEstadoDifere() throws Exception {
        Product laptop = product(1L);
        State ca = state("CA");
        State ny = state("NY");
        TaxRate rate = new TaxRate(laptop, ca, new BigDecimal("7.25"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), null));

        TaxRule rule = new EffectivePeriodTaxRule(rate);

        assertThat(rule.appliesTo(laptop, ny, LocalDate.of(2024, 6, 1))).isFalse();
    }

    @Test
    void naoAplicaForaDoPeriodo() throws Exception {
        Product laptop = product(1L);
        State ca = state("CA");
        TaxRate rate = new TaxRate(laptop, ca, new BigDecimal("7.25"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1)));

        TaxRule rule = new EffectivePeriodTaxRule(rate);

        assertThat(rule.appliesTo(laptop, ca, LocalDate.of(2025, 6, 1))).isFalse();
        assertThat(rule.appliesTo(laptop, ca, LocalDate.of(2023, 12, 31))).isFalse();
    }

    private Product product(Long id) throws Exception {
        Product p = new Product("X", new BigDecimal("100"));
        Field f = Product.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(p, id);
        return p;
    }

    private State state(String code) {
        return new State(code, code);
    }
}
