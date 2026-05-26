package com.poc.taxsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class JsonTaxRuleProvider implements CompoundTaxLayer {

    private final List<JsonRate> loaded;

    public JsonTaxRuleProvider(ObjectMapper mapper,
                               @Value("classpath:rates.json") Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            this.loaded = mapper.readValue(is, JsonRatesFile.class).rates();
        }
    }

    @Override
    public List<TaxRule> rulesFor(Product product, State state, LocalDate when) {
        return loaded.stream()
                .filter(r -> r.productId().equals(product.getId())
                        && r.stateCode().equals(state.getCode())
                        && !when.isBefore(r.from())
                        && (r.to() == null || when.isBefore(r.to())))
                .<TaxRule>map(r -> new FlatPercentTaxRule(r.percent()))
                .toList();
    }

    record JsonRate(Long productId, String stateCode, BigDecimal percent, LocalDate from, LocalDate to) {
    }

    record JsonRatesFile(List<JsonRate> rates) {
    }
}
