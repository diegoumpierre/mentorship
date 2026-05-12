package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.model.TaxRate;
import com.poc.taxsystem.repository.TaxRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaxEngine {

    static final RoundingMode TAX_ROUNDING = RoundingMode.HALF_UP;
    static final int MONEY_SCALE = 2;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final TaxRateRepository rates;

    public TaxEngine(TaxRateRepository rates) {
        this.rates = rates;
    }

    public BigDecimal taxFor(Product product, State state, LocalDate when) {
        TaxRate rate = findRate(product, state, when);
        return product.getPrice()
                .multiply(rate.getPercent())
                .divide(HUNDRED, MONEY_SCALE, TAX_ROUNDING);
    }

    private TaxRate findRate(Product product, State state, LocalDate when) {
        List<TaxRate> applicable = rates
                .findByProductIdAndStateCode(product.getId(), state.getCode())
                .stream()
                .filter(r -> r.getPeriod().contains(when))
                .toList();
        if (applicable.isEmpty()) {
            throw new NoApplicableRateException(product, state, when);
        }
        if (applicable.size() > 1) {
            throw new AmbiguousRateException(product, state, when);
        }
        return applicable.get(0);
    }
}
