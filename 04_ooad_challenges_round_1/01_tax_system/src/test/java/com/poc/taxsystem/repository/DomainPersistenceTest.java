package com.poc.taxsystem.repository;

import com.poc.taxsystem.model.TaxRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DomainPersistenceTest {

    @Autowired
    private ProductRepository products;

    @Autowired
    private StateRepository states;

    @Autowired
    private TaxRateRepository rates;

    @Test
    void seedCarregouProdutos() {
        assertEquals(3, products.count());
    }

    @Test
    void seedCarregouStates() {
        assertEquals(3, states.count());
    }

    @Test
    void seedCarregouRates() {
        assertTrue(rates.count() >= 10);
    }

    @Test
    void taxRateRelacionaProductEStateEPeriodo() {
        List<TaxRate> r = rates.findAll();
        assertFalse(r.isEmpty());
        TaxRate first = r.get(0);
        assertNotNull(first.getProduct());
        assertNotNull(first.getState());
        assertNotNull(first.getPercent());
        assertNotNull(first.getPeriod());
        assertNotNull(first.getPeriod().getFrom());
    }

    @Test
    void laptopEmCaTemDoisPeriodos() {
        List<TaxRate> r = rates.findByProductIdAndStateCode(1L, "CA");
        assertEquals(2, r.size());
    }
}
