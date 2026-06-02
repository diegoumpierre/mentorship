package com.poc.logisticfreight.rates;

import java.time.Duration;
import java.time.Instant;

public class StaleRatesException extends RuntimeException {

    public StaleRatesException(Instant asOf, Instant now, Duration staleAfter) {
        super("freight rates are stale: snapshot as of " + asOf
                + ", now " + now + ", allowed age " + staleAfter);
    }
}
