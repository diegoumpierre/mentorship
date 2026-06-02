package com.poc.logisticfreight.pricing.components;

import com.poc.logisticfreight.pricing.PriceComponent;
import com.poc.logisticfreight.pricing.PricingContext;

import java.math.BigDecimal;

public final class DistanceSurchargeComponent implements PriceComponent {

    @Override
    public String name() {
        return "distance";
    }

    @Override
    public BigDecimal amount(PricingContext context) {
        return context.rates().perKilometer().multiply(context.distanceKm());
    }
}
