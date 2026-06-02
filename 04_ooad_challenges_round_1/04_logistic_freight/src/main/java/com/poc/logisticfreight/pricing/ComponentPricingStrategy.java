package com.poc.logisticfreight.pricing;

import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.rates.ModeRates;
import com.poc.logisticfreight.rates.RateSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class ComponentPricingStrategy implements PricingStrategy {

    private final TransportType type;
    private final List<PriceComponent> components;

    public ComponentPricingStrategy(TransportType type, List<PriceComponent> components) {
        this.type = type;
        this.components = List.copyOf(components);
    }

    @Override
    public TransportType type() {
        return type;
    }

    @Override
    public BigDecimal price(Shipment shipment, BigDecimal distanceKm, RateSnapshot snapshot) {
        ModeRates rates = snapshot.ratesFor(type);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (PriceComponent component : components) {
            PricingContext context = new PricingContext(shipment, distanceKm, rates, subtotal);
            subtotal = subtotal.add(component.amount(context));
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }
}
