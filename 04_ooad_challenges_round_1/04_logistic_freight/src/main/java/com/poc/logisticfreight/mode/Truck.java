package com.poc.logisticfreight.mode;

import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.model.Weight;
import com.poc.logisticfreight.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class Truck implements TransportMode {

    private final Weight maxWeight;
    private final BigDecimal maxLongestSideCm;
    private final BigDecimal cruiseSpeedKmh;
    private final PricingStrategy pricingStrategy;

    public Truck(Weight maxWeight, BigDecimal maxLongestSideCm, BigDecimal cruiseSpeedKmh, PricingStrategy pricingStrategy) {
        this.maxWeight = maxWeight;
        this.maxLongestSideCm = maxLongestSideCm;
        this.cruiseSpeedKmh = cruiseSpeedKmh;
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public TransportType type() {
        return TransportType.TRUCK;
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
            reasons.add("shipment over truck max weight of " + maxWeight.kilograms() + " kg");
        }
        if (shipment.dimensions().longestSideCm().compareTo(maxLongestSideCm) > 0) {
            reasons.add("shipment oversize for truck, longest side over " + maxLongestSideCm + " cm");
        }
        if (!shipment.origin().isServedBy(TransportType.TRUCK)) {
            reasons.add("origin " + shipment.origin().code() + " is not reachable by road");
        }
        if (!shipment.destination().isServedBy(TransportType.TRUCK)) {
            reasons.add("destination " + shipment.destination().code() + " is not reachable by road");
        }
        return reasons;
    }
}
