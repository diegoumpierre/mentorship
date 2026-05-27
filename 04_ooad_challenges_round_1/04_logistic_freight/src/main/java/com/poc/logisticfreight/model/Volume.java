package com.poc.logisticfreight.model;

import java.math.BigDecimal;

public record Volume(BigDecimal cubicMeters) {

    public Volume {
        if (cubicMeters == null) {
            throw new IllegalArgumentException("volume is required");
        }
        if (cubicMeters.signum() < 0) {
            throw new IllegalArgumentException("volume cannot be negative: " + cubicMeters);
        }
    }
}
