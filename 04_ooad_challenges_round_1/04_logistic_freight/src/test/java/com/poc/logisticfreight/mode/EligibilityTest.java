package com.poc.logisticfreight.mode;

import com.poc.logisticfreight.model.Dimensions;
import com.poc.logisticfreight.model.Location;
import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.model.Weight;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EligibilityTest {

    private final Location seaport = new Location("SEA", Set.of(TransportType.BOAT, TransportType.TRUCK, TransportType.RAIL));
    private final Location inland = new Location("SLC", Set.of(TransportType.TRUCK));

    private final Truck truck = new Truck(Weight.ofKilograms("24000"), new BigDecimal("250"), new BigDecimal("70"), null);
    private final Rail rail = new Rail(Weight.ofKilograms("90000"), new BigDecimal("60"), null);
    private final Boat boat = new Boat(Weight.ofKilograms("500000"), new BigDecimal("35"), null);

    @Test
    void cargaOversizeEhRejeitadaPeloCaminhao() {
        Shipment oversize = shipment(Dimensions.ofCentimeters("300", "100", "100"), "1000", seaport, seaport);

        assertThat(truck.isEligibleFor(oversize)).isFalse();
        assertThat(truck.rejectionReasons(oversize)).anyMatch(reason -> reason.contains("oversize"));
    }

    @Test
    void semTerminalFerroviarioNoDestinoRejeitaTrem() {
        Shipment toInland = shipment(Dimensions.ofCentimeters("100", "100", "100"), "1000", seaport, inland);

        assertThat(rail.isEligibleFor(toInland)).isFalse();
        assertThat(rail.rejectionReasons(toInland)).anyMatch(reason -> reason.contains("rail terminal"));
    }

    @Test
    void cargaAcimaDoPesoMaximoEhRejeitada() {
        Shipment tooHeavy = shipment(Dimensions.ofCentimeters("100", "100", "100"), "30000", seaport, seaport);

        assertThat(truck.isEligibleFor(tooHeavy)).isFalse();
        assertThat(truck.rejectionReasons(tooHeavy)).anyMatch(reason -> reason.contains("max weight"));
    }

    @Test
    void cargaDentroDosLimitesEhElegivelEmTodosOsModos() {
        Shipment ok = shipment(Dimensions.ofCentimeters("100", "100", "100"), "1000", seaport, seaport);

        assertThat(truck.isEligibleFor(ok)).isTrue();
        assertThat(rail.isEligibleFor(ok)).isTrue();
        assertThat(boat.isEligibleFor(ok)).isTrue();
    }

    private Shipment shipment(Dimensions dimensions, String weightKg, Location origin, Location destination) {
        return new Shipment(dimensions, Weight.ofKilograms(weightKg), new BigDecimal("5000"), origin, destination, false);
    }
}
