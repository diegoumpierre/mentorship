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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TaxEngineTest {

    @Autowired
    private TaxEngine engine;

    @Autowired
    private ProductRepository products;

    @Autowired
    private StateRepository states;

    @Autowired
    private TaxRateRepository rates;

    @Test
    void laptopEmCaliforniaEm2024() {
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        BigDecimal tax = engine.taxFor(laptop, ca, LocalDate.of(2024, 6, 1));
        // 1500 * 7.25 / 100 = 108.75
        assertThat(tax).isEqualByComparingTo("108.75");
    }

    @Test
    void laptopEmCaliforniaEm2025AplicaNovoPercent() {
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        BigDecimal tax = engine.taxFor(laptop, ca, LocalDate.of(2025, 6, 1));
        // 1500 * 7.5 / 100 = 112.50
        assertThat(tax).isEqualByComparingTo("112.50");
    }

    @Test
    void mesmoProdutoEstadoDiferenteAplicaPercentDiferente() {
        Product laptop = products.findById(1L).orElseThrow();
        State ny = states.findById("NY").orElseThrow();
        BigDecimal tax = engine.taxFor(laptop, ny, LocalDate.of(2024, 6, 1));
        // 1500 * 8.875 / 100 = 133.125 → 133.13 HALF_UP
        assertThat(tax).isEqualByComparingTo("133.13");
    }

    @Test
    void breadIsentoEmTxRetornaZero() {
        Product bread = products.findById(3L).orElseThrow();
        State tx = states.findById("TX").orElseThrow();
        BigDecimal tax = engine.taxFor(bread, tx, LocalDate.of(2024, 6, 1));
        assertThat(tax).isEqualByComparingTo("0.00");
    }

    @Test
    void dataAntesDoPrimeiroPeriodoFalha() {
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        assertThatThrownBy(() -> engine.taxFor(laptop, ca, LocalDate.of(2023, 12, 31)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Sem tax rate vigente");
    }

    @Test
    void boundaryEntreDoisPeriodosUsaONovo() {
        // periodo antigo: [2024-01-01, 2025-01-01) - exclusivo no "to"
        // periodo novo:   [2025-01-01, null)        - inclusivo no "from"
        // pra 2025-01-01 o vigente eh o novo (7.5%).
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        BigDecimal tax = engine.taxFor(laptop, ca, LocalDate.of(2025, 1, 1));
        assertThat(tax).isEqualByComparingTo("112.50");
    }

    @Test
    @Transactional
    void produtoSemRateLancaErro() {
        Product gadget = products.save(new Product("Gadget", new BigDecimal("100.00")));
        State ca = states.findById("CA").orElseThrow();
        assertThatThrownBy(() -> engine.taxFor(gadget, ca, LocalDate.of(2024, 6, 1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Sem tax rate vigente");
    }

    @Test
    @Transactional
    void periodosSobrepostosLancamErro() {
        Product gadget = products.save(new Product("Overlap", new BigDecimal("100.00")));
        State ca = states.findById("CA").orElseThrow();
        rates.save(new TaxRate(gadget, ca, new BigDecimal("5.0000"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1))));
        rates.save(new TaxRate(gadget, ca, new BigDecimal("6.0000"),
                new EffectivePeriod(LocalDate.of(2024, 6, 1), LocalDate.of(2025, 6, 1))));
        assertThatThrownBy(() -> engine.taxFor(gadget, ca, LocalDate.of(2024, 9, 1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Mais de uma tax rate vigente");
    }

    @Test
    @Transactional
    void dataNoGapEntrePeriodosFalha() {
        Product gadget = products.save(new Product("Gap", new BigDecimal("100.00")));
        State ca = states.findById("CA").orElseThrow();
        rates.save(new TaxRate(gadget, ca, new BigDecimal("5.0000"),
                new EffectivePeriod(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 7, 1))));
        rates.save(new TaxRate(gadget, ca, new BigDecimal("6.0000"),
                new EffectivePeriod(LocalDate.of(2025, 1, 1), null)));
        assertThatThrownBy(() -> engine.taxFor(gadget, ca, LocalDate.of(2024, 10, 1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Sem tax rate vigente");
    }
}
