package com.poc.logisticfreight.rates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.logisticfreight.model.TransportType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class JsonRateProvider implements RateProvider {

    private final ObjectMapper mapper;
    private final Supplier<InputStream> source;

    public JsonRateProvider(ObjectMapper mapper, Supplier<InputStream> source) {
        this.mapper = mapper;
        this.source = source;
    }

    @Override
    public RateSnapshot currentSnapshot() {
        try (InputStream input = source.get()) {
            RatesFile file = mapper.readValue(input, RatesFile.class);
            Map<TransportType, ModeRates> byMode = new EnumMap<>(TransportType.class);
            file.modes().forEach((mode, rate) -> byMode.put(TransportType.valueOf(mode), rate.toModeRates()));
            return new RateSnapshot(file.asOf(), byMode);
        } catch (IOException e) {
            throw new UncheckedIOException("could not read freight rates", e);
        }
    }

    record RatesFile(Instant asOf, Map<String, JsonModeRate> modes) {
    }

    record JsonModeRate(
            BigDecimal baseFare,
            BigDecimal perCubicMeter,
            BigDecimal perKilogram,
            BigDecimal perKilometer,
            BigDecimal fuelSurchargePercent,
            BigDecimal hazardSurcharge) {

        ModeRates toModeRates() {
            return new ModeRates(baseFare, perCubicMeter, perKilogram, perKilometer, fuelSurchargePercent, hazardSurcharge);
        }
    }
}
