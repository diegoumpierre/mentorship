package com.poc.logisticfreight.model;

import java.math.BigDecimal;

public record Shipment(
        Dimensions dimensions,
        Weight weight,
        BigDecimal declaredValue,
        Location origin,
        Location destination,
        boolean hazardous) {

    public Shipment {
        if (dimensions == null) {
            throw new IllegalArgumentException("dimensions are required");
        }
        if (weight == null) {
            throw new IllegalArgumentException("weight is required");
        }
        if (declaredValue == null || declaredValue.signum() < 0) {
            throw new IllegalArgumentException("declared value cannot be negative: " + declaredValue);
        }
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("origin and destination are required");
        }
    }

    public Volume volume() {
        return dimensions.volume();
    }
}
