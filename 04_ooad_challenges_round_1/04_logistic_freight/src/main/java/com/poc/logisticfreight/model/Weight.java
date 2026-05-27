package com.poc.logisticfreight.model;

import java.math.BigDecimal;

public record Weight(BigDecimal kilograms) {

    public Weight {
        if (kilograms == null) {
            throw new IllegalArgumentException("weight is required");
        }
        if (kilograms.signum() < 0) {
            throw new IllegalArgumentException("weight cannot be negative: " + kilograms);
        }
    }

    public static Weight ofKilograms(String kilograms) {
        return new Weight(new BigDecimal(kilograms));
    }

    public boolean isHeavierThan(Weight other) {
        return kilograms.compareTo(other.kilograms) > 0;
    }
}
