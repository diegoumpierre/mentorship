package com.poc.logisticfreight.pricing;

import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.rates.RateSnapshot;

import java.math.BigDecimal;

public interface PricingStrategy {

    TransportType type();

    BigDecimal price(Shipment shipment, BigDecimal distanceKm, RateSnapshot snapshot);
}
