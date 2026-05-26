package com.poc.taxsystem.repository;

import com.poc.taxsystem.model.EffectivePeriod;
import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.model.TaxRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TaxRateUniquenessTest {

    @Autowired
    private ProductRepository products;

    @Autowired
    private StateRepository states;

    @Autowired
    private TaxRateRepository rates;

    @Test
    void mesmoProdutoEstadoEDataDeInicioNaoPodeRepetir() {
        Product p = products.save(new Product("Dup", new BigDecimal("10.00")));
        State ca = states.findById("CA").orElseThrow();
        LocalDate from = LocalDate.of(2030, 1, 1);

        rates.saveAndFlush(new TaxRate(p, ca, new BigDecimal("5.0000"),
                new EffectivePeriod(from, LocalDate.of(2031, 1, 1))));

        assertThatThrownBy(() -> rates.saveAndFlush(new TaxRate(p, ca, new BigDecimal("6.0000"),
                new EffectivePeriod(from, LocalDate.of(2032, 1, 1)))))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
