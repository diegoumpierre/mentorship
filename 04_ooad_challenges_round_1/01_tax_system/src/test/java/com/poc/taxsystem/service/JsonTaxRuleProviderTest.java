package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonTaxRuleProviderTest {

    @Autowired
    private TaxEngine engine;

    @Autowired
    private ProductRepository products;

    @Test
    void washingtonVemDoArquivoJson() {
        Product laptop = products.findById(1L).orElseThrow();
        State wa = new State("WA", "Washington");

        BigDecimal tax = engine.taxFor(laptop, wa, LocalDate.of(2024, 6, 1));

        // 1500 * 6.5 / 100 = 97.50
        assertThat(tax).isEqualByComparingTo("97.50");
    }

    @Test
    void breadEmWashingtonIsentoPeloJson() {
        Product bread = products.findById(3L).orElseThrow();
        State wa = new State("WA", "Washington");

        BigDecimal tax = engine.taxFor(bread, wa, LocalDate.of(2024, 6, 1));

        assertThat(tax).isEqualByComparingTo("0.00");
    }
}
