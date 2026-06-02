package com.poc.logisticfreight.pricing.components;

import com.poc.logisticfreight.pricing.PriceComponent;
import com.poc.logisticfreight.pricing.PricingContext;

import java.math.BigDecimal;

public final class HazardSurchargeComponent implements PriceComponent {

    @Override
    public String name() {
        return "hazard";
    }

    @Override
    public BigDecimal amount(PricingContext context) {
        if (!context.shipment().hazardous()) {
            return BigDecimal.ZERO;
        }
        return context.rates().hazardSurcharge();
    }
}
