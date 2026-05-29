package com.poc.logisticfreight.mode;

import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public sealed interface TransportMode permits Boat, Truck, Rail {

    TransportType type();

    PricingStrategy pricingStrategy();

    Duration eta(BigDecimal distanceKm);

    List<String> rejectionReasons(Shipment shipment);

    default boolean isEligibleFor(Shipment shipment) {
        return rejectionReasons(shipment).isEmpty();
    }
}
