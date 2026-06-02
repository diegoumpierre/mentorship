package com.poc.logisticfreight.rates;

import com.poc.logisticfreight.model.TransportType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CachingRateProviderTest {

    private final Instant asOf = Instant.parse("2026-06-01T00:00:00Z");

    @Test
    void naoVaiNaFonteDeNovoEnquantoOSnapshotEstaFresco() {
        CountingRateProvider source = new CountingRateProvider(snapshotAsOf(asOf));
        Clock clock = Clock.fixed(asOf.plus(Duration.ofHours(2)), ZoneOffset.UTC);
        CachingRateProvider caching = new CachingRateProvider(source, clock, Duration.ofDays(1));

        caching.currentSnapshot();
        caching.currentSnapshot();

        assertThat(source.calls).isEqualTo(1);
    }

    @Test
    void recusaQuoteComSnapshotVencidoEmVezDeUsarStale() {
        CountingRateProvider source = new CountingRateProvider(snapshotAsOf(asOf));
        Clock clock = Clock.fixed(asOf.plus(Duration.ofDays(5)), ZoneOffset.UTC);
        CachingRateProvider caching = new CachingRateProvider(source, clock, Duration.ofDays(1));

        assertThatThrownBy(caching::currentSnapshot).isInstanceOf(StaleRatesException.class);
    }

    private RateSnapshot snapshotAsOf(Instant timestamp) {
        return new RateSnapshot(timestamp, Map.of(TransportType.TRUCK, new ModeRates(
                new BigDecimal("80"), new BigDecimal("25"), new BigDecimal("0.4"),
                new BigDecimal("0.9"), new BigDecimal("12"), new BigDecimal("150"))));
    }

    private static final class CountingRateProvider implements RateProvider {

        private final RateSnapshot snapshot;
        private int calls;

        private CountingRateProvider(RateSnapshot snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        public RateSnapshot currentSnapshot() {
            calls++;
            return snapshot;
        }
    }
}
