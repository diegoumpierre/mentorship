package com.poc.logisticfreight.mode;

import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.model.Weight;
import com.poc.logisticfreight.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class Boat implements TransportMode {

    private final Weight maxWeight;
    private final BigDecimal cruiseSpeedKmh;
    private final PricingStrategy pricingStrategy;

    public Boat(Weight maxWeight, BigDecimal cruiseSpeedKmh, PricingStrategy pricingStrategy) {
        this.maxWeight = maxWeight;
        this.cruiseSpeedKmh = cruiseSpeedKmh;
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public TransportType type() {
        return TransportType.BOAT;
    }

    @Override
    public PricingStrategy pricingStrategy() {
        return pricingStrategy;
    }

    @Override
    public Duration eta(BigDecimal distanceKm) {
        return Eta.atCruiseSpeed(distanceKm, cruiseSpeedKmh);
    }

    @Override
    public List<String> rejectionReasons(Shipment shipment) {
        List<String> reasons = new ArrayList<>();
        if (shipment.weight().isHeavierThan(maxWeight)) {
            reasons.add("shipment over boat max weight of " + maxWeight.kilograms() + " kg");
        }
        if (!shipment.origin().isServedBy(TransportType.BOAT)) {
            reasons.add("origin " + shipment.origin().code() + " has no port");
        }
        if (!shipment.destination().isServedBy(TransportType.BOAT)) {
            reasons.add("destination " + shipment.destination().code() + " has no port");
        }
        return reasons;
    }
}
