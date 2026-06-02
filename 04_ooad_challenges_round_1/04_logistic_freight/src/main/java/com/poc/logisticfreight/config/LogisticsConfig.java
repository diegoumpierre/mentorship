package com.poc.logisticfreight.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.logisticfreight.mode.Airplane;
import com.poc.logisticfreight.mode.Boat;
import com.poc.logisticfreight.mode.Rail;
import com.poc.logisticfreight.mode.TransportMode;
import com.poc.logisticfreight.mode.Truck;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.model.Weight;
import com.poc.logisticfreight.pricing.ComponentPricingStrategy;
import com.poc.logisticfreight.pricing.PriceComponent;
import com.poc.logisticfreight.pricing.PricingStrategy;
import com.poc.logisticfreight.pricing.components.BaseFareComponent;
import com.poc.logisticfreight.pricing.components.DistanceSurchargeComponent;
import com.poc.logisticfreight.pricing.components.FuelSurchargeComponent;
import com.poc.logisticfreight.pricing.components.HazardSurchargeComponent;
import com.poc.logisticfreight.pricing.components.VolumeComponent;
import com.poc.logisticfreight.pricing.components.WeightComponent;
import com.poc.logisticfreight.rates.CachingRateProvider;
import com.poc.logisticfreight.rates.JsonRateProvider;
import com.poc.logisticfreight.rates.RateProvider;
import com.poc.logisticfreight.quote.DistanceBook;
import com.poc.logisticfreight.quote.InMemoryDistanceBook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.util.List;

@Configuration
public class LogisticsConfig {

    @Bean
    public TransportMode truck() {
        return new Truck(
                Weight.ofKilograms("24000"),
                new BigDecimal("250"),
                new BigDecimal("70"),
                standardPricing(TransportType.TRUCK));
    }

    @Bean
    public TransportMode rail() {
        return new Rail(
                Weight.ofKilograms("90000"),
                new BigDecimal("60"),
                standardPricing(TransportType.RAIL));
    }

    @Bean
    public TransportMode boat() {
        return new Boat(
                Weight.ofKilograms("500000"),
                new BigDecimal("35"),
                standardPricing(TransportType.BOAT));
    }

    @Bean
    public TransportMode airplane() {
        return new Airplane(
                Weight.ofKilograms("12000"),
                new BigDecimal("800"),
                standardPricing(TransportType.AIRPLANE));
    }

    @Bean
    public RateProvider rateProvider(ObjectMapper mapper) {
        JsonRateProvider source = new JsonRateProvider(mapper, LogisticsConfig::ratesStream);
        return new CachingRateProvider(source, Clock.systemUTC(), Duration.ofDays(3));
    }

    @Bean
    public DistanceBook distanceBook() {
        return new InMemoryDistanceBook()
                .put("SEA", "POR", new BigDecimal("280"))
                .put("SEA", "SLC", new BigDecimal("1310"))
                .put("POR", "SLC", new BigDecimal("1190"));
    }

    private static PricingStrategy standardPricing(TransportType type) {
        return new ComponentPricingStrategy(type, standardChain());
    }

    private static List<PriceComponent> standardChain() {
        return List.of(
                new BaseFareComponent(),
                new VolumeComponent(),
                new WeightComponent(),
                new DistanceSurchargeComponent(),
                new FuelSurchargeComponent(),
                new HazardSurchargeComponent());
    }

    private static InputStream ratesStream() {
        try {
            return new ClassPathResource("rates.json").getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException("could not open rates.json", e);
        }
    }
}
