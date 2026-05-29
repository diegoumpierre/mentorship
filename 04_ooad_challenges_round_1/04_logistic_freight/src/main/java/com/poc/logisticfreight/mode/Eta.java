package com.poc.logisticfreight.mode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

final class Eta {

    private static final BigDecimal MINUTES_PER_HOUR = new BigDecimal("60");

    private Eta() {
    }

    static Duration atCruiseSpeed(BigDecimal distanceKm, BigDecimal cruiseSpeedKmh) {
        BigDecimal hours = distanceKm.divide(cruiseSpeedKmh, 6, RoundingMode.HALF_UP);
        long minutes = hours.multiply(MINUTES_PER_HOUR).setScale(0, RoundingMode.HALF_UP).longValueExact();
        return Duration.ofMinutes(minutes);
    }
}
