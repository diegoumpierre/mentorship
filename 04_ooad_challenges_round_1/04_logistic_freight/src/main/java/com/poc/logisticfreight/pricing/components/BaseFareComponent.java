package com.poc.logisticfreight.pricing.components;

import com.poc.logisticfreight.pricing.PriceComponent;
import com.poc.logisticfreight.pricing.PricingContext;

import java.math.BigDecimal;

public final class BaseFareComponent implements PriceComponent {

    @Override
    public String name() {
        return "base-fare";
    }

    @Override
    public BigDecimal amount(PricingContext context) {
        return context.rates().baseFare();
    }
}
