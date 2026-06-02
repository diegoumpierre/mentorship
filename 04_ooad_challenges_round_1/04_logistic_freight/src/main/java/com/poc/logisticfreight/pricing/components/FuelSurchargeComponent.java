package com.poc.logisticfreight.pricing.components;

import com.poc.logisticfreight.pricing.PriceComponent;
import com.poc.logisticfreight.pricing.PricingContext;

import java.math.BigDecimal;

public final class FuelSurchargeComponent implements PriceComponent {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Override
    public String name() {
        return "fuel";
    }

    @Override
    public BigDecimal amount(PricingContext context) {
        BigDecimal percent = context.rates().fuelSurchargePercent();
        return context.runningSubtotal().multiply(percent).divide(HUNDRED);
    }
}
