package com.poc.logisticfreight.pricing.components;

import com.poc.logisticfreight.pricing.PriceComponent;
import com.poc.logisticfreight.pricing.PricingContext;

import java.math.BigDecimal;

public final class WeightComponent implements PriceComponent {

    @Override
    public String name() {
        return "per-weight";
    }

    @Override
    public BigDecimal amount(PricingContext context) {
        BigDecimal kilograms = context.shipment().weight().kilograms();
        return context.rates().perKilogram().multiply(kilograms);
    }
}
