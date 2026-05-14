package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Invoice;
import com.poc.taxsystem.model.InvoiceTotal;
import com.poc.taxsystem.model.LineItem;
import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.repository.ProductRepository;
import com.poc.taxsystem.repository.StateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class InvoiceTotalTest {

    @Autowired
    private TaxEngine engine;

    @Autowired
    private ProductRepository products;

    @Autowired
    private StateRepository states;

    @Test
    void invoiceComUmItem() {
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        Invoice invoice = new Invoice(ca, LocalDate.of(2024, 6, 1),
                List.of(new LineItem(laptop, 1)));

        InvoiceTotal total = engine.totalFor(invoice);

        assertThat(total.subtotal()).isEqualByComparingTo("1500.00");
        assertThat(total.tax()).isEqualByComparingTo("108.75");
        assertThat(total.total()).isEqualByComparingTo("1608.75");
    }

    @Test
    void invoiceComMultiplosItens() {
        Product laptop = products.findById(1L).orElseThrow();
        Product shirt = products.findById(2L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        Invoice invoice = new Invoice(ca, LocalDate.of(2024, 6, 1), List.of(
                new LineItem(laptop, 1),
                new LineItem(shirt, 2)
        ));

        InvoiceTotal total = engine.totalFor(invoice);

        // subtotal: 1500 + (30 * 2) = 1560
        // tax laptop: 1500 * 7.25 / 100 = 108.75
        // tax shirts: 60 * 7.25 / 100 = 4.35
        // tax total: 113.10
        // total: 1673.10
        assertThat(total.subtotal()).isEqualByComparingTo("1560.00");
        assertThat(total.tax()).isEqualByComparingTo("113.10");
        assertThat(total.total()).isEqualByComparingTo("1673.10");
    }

    @Test
    void invoiceMisturaItemTributadoEIsentoEmTexas() {
        Product bread = products.findById(3L).orElseThrow();
        Product laptop = products.findById(1L).orElseThrow();
        State tx = states.findById("TX").orElseThrow();
        Invoice invoice = new Invoice(tx, LocalDate.of(2024, 6, 1), List.of(
                new LineItem(bread, 5),
                new LineItem(laptop, 1)
        ));

        InvoiceTotal total = engine.totalFor(invoice);

        // bread em TX: rate 0%, qualquer quantidade nao adiciona imposto
        // laptop em TX: 1500 * 6.25 / 100 = 93.75
        assertThat(total.subtotal()).isEqualByComparingTo("1525.00");
        assertThat(total.tax()).isEqualByComparingTo("93.75");
        assertThat(total.total()).isEqualByComparingTo("1618.75");
    }

    @Test
    void mesmosItensEmEstadosDiferentesGeramTotalsDiferentes() {
        Product laptop = products.findById(1L).orElseThrow();
        State ca = states.findById("CA").orElseThrow();
        State ny = states.findById("NY").orElseThrow();
        LocalDate when = LocalDate.of(2024, 6, 1);
        List<LineItem> items = List.of(new LineItem(laptop, 1));

        InvoiceTotal caTotal = engine.totalFor(new Invoice(ca, when, items));
        InvoiceTotal nyTotal = engine.totalFor(new Invoice(ny, when, items));

        assertThat(caTotal.tax()).isEqualByComparingTo("108.75");
        assertThat(nyTotal.tax()).isEqualByComparingTo("133.13");
    }

    @Test
    @Transactional
    void invoiceComItemSemRatePropagaExcecao() {
        Product gadget = products.save(new Product("Gadget", new BigDecimal("100.00")));
        State ca = states.findById("CA").orElseThrow();
        Invoice invoice = new Invoice(ca, LocalDate.of(2024, 6, 1),
                List.of(new LineItem(gadget, 1)));

        assertThatThrownBy(() -> engine.totalFor(invoice))
                .isInstanceOf(NoApplicableRateException.class);
    }
}
