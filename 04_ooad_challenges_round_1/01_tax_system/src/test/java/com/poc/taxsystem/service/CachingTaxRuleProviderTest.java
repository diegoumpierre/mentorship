package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CachingTaxRuleProviderTest {

    @Test
    void segundoLookupNaoChamaDelegate() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        TaxRuleProvider fake = (p, s, w) -> {
            calls.incrementAndGet();
            return List.of();
        };
        CachingTaxRuleProvider cache = new CachingTaxRuleProvider(fake);
        Product p = product(1L);
        State ca = state("CA");
        LocalDate when = LocalDate.of(2024, 6, 1);

        cache.rulesFor(p, ca, when);
        cache.rulesFor(p, ca, when);

        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void invalidatePorProdutoEstadoForcaRecarga() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        TaxRuleProvider fake = (p, s, w) -> {
            calls.incrementAndGet();
            return List.of();
        };
        CachingTaxRuleProvider cache = new CachingTaxRuleProvider(fake);
        Product p = product(1L);
        State ca = state("CA");
        LocalDate when = LocalDate.of(2024, 6, 1);

        cache.rulesFor(p, ca, when);
        cache.invalidate(1L, "CA");
        cache.rulesFor(p, ca, when);

        assertThat(calls.get()).isEqualTo(2);
    }

    @Test
    void invalidateNaoMexeEmOutroProduto() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        TaxRuleProvider fake = (p, s, w) -> {
            calls.incrementAndGet();
            return List.of();
        };
        CachingTaxRuleProvider cache = new CachingTaxRuleProvider(fake);
        Product p1 = product(1L);
        Product p2 = product(2L);
        State ca = state("CA");
        LocalDate when = LocalDate.of(2024, 6, 1);

        cache.rulesFor(p1, ca, when);
        cache.rulesFor(p2, ca, when);
        cache.invalidate(1L, "CA");
        cache.rulesFor(p1, ca, when);
        cache.rulesFor(p2, ca, when);

        assertThat(calls.get()).isEqualTo(3);
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
