package com.poc.logisticfreight.rates;

import com.poc.logisticfreight.model.TransportType;

import java.time.Instant;
import java.util.Map;

public record RateSnapshot(Instant asOf, Map<TransportType, ModeRates> ratesByMode) {

    public RateSnapshot {
        if (asOf == null) {
            throw new IllegalArgumentException("snapshot needs an as-of timestamp");
        }
        ratesByMode = ratesByMode == null ? Map.of() : Map.copyOf(ratesByMode);
    }

    public ModeRates ratesFor(TransportType type) {
        ModeRates rates = ratesByMode.get(type);
        if (rates == null) {
            throw new IllegalStateException("no rates for mode " + type + " in snapshot as of " + asOf);
        }
        return rates;
    }
}
