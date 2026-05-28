package com.poc.logisticfreight.rates;

import java.math.BigDecimal;

public record ModeRates(
        BigDecimal baseFare,
        BigDecimal perCubicMeter,
        BigDecimal perKilogram,
        BigDecimal perKilometer,
        BigDecimal fuelSurchargePercent,
        BigDecimal hazardSurcharge) {
}
