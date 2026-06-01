package com.poc.logisticfreight.rates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.logisticfreight.model.TransportType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JsonRateProviderTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private static final String JSON = """
            {
              "asOf": "2026-06-01T00:00:00Z",
              "modes": {
                "TRUCK": {
                  "baseFare": 80.00,
                  "perCubicMeter": 25.00,
                  "perKilogram": 0.40,
                  "perKilometer": 0.90,
                  "fuelSurchargePercent": 12.00,
                  "hazardSurcharge": 150.00
                }
              }
            }
            """;

    @Test
    void leOTimestampAsOfEAsTarifasDoJson() {
        JsonRateProvider provider = new JsonRateProvider(
                mapper,
                () -> new ByteArrayInputStream(JSON.getBytes(StandardCharsets.UTF_8)));

        RateSnapshot snapshot = provider.currentSnapshot();

        assertThat(snapshot.asOf()).isEqualTo(Instant.parse("2026-06-01T00:00:00Z"));
        assertThat(snapshot.ratesFor(TransportType.TRUCK).baseFare()).isEqualByComparingTo("80.00");
        assertThat(snapshot.ratesFor(TransportType.TRUCK).fuelSurchargePercent()).isEqualByComparingTo("12.00");
    }
}
