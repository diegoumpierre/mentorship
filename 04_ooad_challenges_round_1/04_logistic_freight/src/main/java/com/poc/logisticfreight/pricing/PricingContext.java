package com.poc.logisticfreight.pricing;

import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.rates.ModeRates;

import java.math.BigDecimal;

public record PricingContext(
        Shipment shipment,
        BigDecimal distanceKm,
        ModeRates rates,
        BigDecimal runningSubtotal) {
}
