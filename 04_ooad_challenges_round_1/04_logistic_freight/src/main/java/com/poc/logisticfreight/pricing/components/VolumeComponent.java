package com.poc.logisticfreight.pricing.components;

import com.poc.logisticfreight.pricing.PriceComponent;
import com.poc.logisticfreight.pricing.PricingContext;

import java.math.BigDecimal;

public final class VolumeComponent implements PriceComponent {

    @Override
    public String name() {
        return "per-volume";
    }

    @Override
    public BigDecimal amount(PricingContext context) {
        BigDecimal cubicMeters = context.shipment().volume().cubicMeters();
        return context.rates().perCubicMeter().multiply(cubicMeters);
    }
}
