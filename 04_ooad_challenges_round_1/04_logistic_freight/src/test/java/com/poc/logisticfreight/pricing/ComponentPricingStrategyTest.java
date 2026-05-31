package com.poc.logisticfreight.pricing;

import com.poc.logisticfreight.model.Dimensions;
import com.poc.logisticfreight.model.Location;
import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.model.Weight;
import com.poc.logisticfreight.pricing.components.BaseFareComponent;
import com.poc.logisticfreight.pricing.components.DistanceSurchargeComponent;
import com.poc.logisticfreight.pricing.components.FuelSurchargeComponent;
import com.poc.logisticfreight.pricing.components.HazardSurchargeComponent;
import com.poc.logisticfreight.pricing.components.VolumeComponent;
import com.poc.logisticfreight.pricing.components.WeightComponent;
import com.poc.logisticfreight.rates.ModeRates;
import com.poc.logisticfreight.rates.RateSnapshot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentPricingStrategyTest {

    private final ComponentPricingStrategy truckPricing = new ComponentPricingStrategy(
            TransportType.TRUCK,
            List.of(
                    new BaseFareComponent(),
                    new VolumeComponent(),
                    new WeightComponent(),
                    new DistanceSurchargeComponent(),
                    new FuelSurchargeComponent(),
                    new HazardSurchargeComponent()));

    private final RateSnapshot snapshot = new RateSnapshot(
            Instant.parse("2026-06-01T00:00:00Z"),
            Map.of(TransportType.TRUCK, new ModeRates(
                    new BigDecimal("100"),
                    new BigDecimal("20"),
                    new BigDecimal("0.5"),
                    new BigDecimal("1"),
                    new BigDecimal("10"),
                    new BigDecimal("200"))));

    @Test
    void somaACadeiaNaOrdemBaseVolumePesoDistanciaCombustivel() {
        Shipment shipment = shipment(false);

        // base 100 + volume 20*1 + peso 0.5*500 + distancia 1*300 = 670; combustivel 10% = 67
        assertThat(truckPricing.price(shipment, new BigDecimal("300"), snapshot)).isEqualByComparingTo("737.00");
    }

    @Test
    void cargaPerigosaSomaSobretaxaDepoisDoCombustivel() {
        Shipment hazardous = shipment(true);

        // 737 + hazard 200 = 937 (hazard entra depois do combustivel)
        assertThat(truckPricing.price(hazardous, new BigDecimal("300"), snapshot)).isEqualByComparingTo("937.00");
    }

    @Test
    void mesmaCargaEMesmoSnapshotDaoSempreOMesmoNumero() {
        Shipment shipment = shipment(false);

        BigDecimal first = truckPricing.price(shipment, new BigDecimal("300"), snapshot);
        BigDecimal second = truckPricing.price(shipment, new BigDecimal("300"), snapshot);

        assertThat(first).isEqualByComparingTo(second);
    }

    private Shipment shipment(boolean hazardous) {
        Location hub = new Location("SEA", Set.of(TransportType.TRUCK));
        return new Shipment(
                Dimensions.ofCentimeters("100", "100", "100"),
                Weight.ofKilograms("500"),
                new BigDecimal("5000"),
                hub,
                hub,
                hazardous);
    }
}
