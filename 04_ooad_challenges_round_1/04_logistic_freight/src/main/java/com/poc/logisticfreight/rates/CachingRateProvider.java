package com.poc.logisticfreight.rates;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class CachingRateProvider implements RateProvider {

    private final RateProvider source;
    private final Clock clock;
    private final Duration staleAfter;

    private RateSnapshot cached;

    public CachingRateProvider(RateProvider source, Clock clock, Duration staleAfter) {
        this.source = source;
        this.clock = clock;
        this.staleAfter = staleAfter;
    }

    @Override
    public synchronized RateSnapshot currentSnapshot() {
        if (cached == null || isStale(cached)) {
            cached = source.currentSnapshot();
        }
        if (isStale(cached)) {
            throw new StaleRatesException(cached.asOf(), clock.instant(), staleAfter);
        }
        return cached;
    }

    private boolean isStale(RateSnapshot snapshot) {
        Duration age = Duration.between(snapshot.asOf(), clock.instant());
        return age.compareTo(staleAfter) > 0;
    }
}
