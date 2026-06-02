package com.poc.logisticfreight.quote;

import com.poc.logisticfreight.mode.TransportMode;
import com.poc.logisticfreight.model.Dimensions;
import com.poc.logisticfreight.model.Location;
import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.model.Weight;
import com.poc.logisticfreight.rates.ModeRates;
import com.poc.logisticfreight.rates.RateProvider;
import com.poc.logisticfreight.rates.RateSnapshot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class QuoteServiceTest {

    private final Location sea = new Location("SEA", Set.of(TransportType.BOAT, TransportType.TRUCK, TransportType.RAIL));
    private final Location por = new Location("POR", Set.of(TransportType.BOAT, TransportType.TRUCK, TransportType.RAIL));
    private final Location inland = new Location("SLC", Set.of(TransportType.TRUCK));

    private final List<TransportMode> modes = List.of(TestModes.truck(), TestModes.rail(), TestModes.boat());

    private final DistanceBook distanceBook = new InMemoryDistanceBook()
            .put("SEA", "POR", new BigDecimal("280"))
            .put("SEA", "SLC", new BigDecimal("1310"));

    @Test
    void cotaTodosOsModosElegiveisOrdenadosPorPreco() {
        QuoteService service = new QuoteService(modes, distanceBook, fixedProvider(baseRates()));
        Shipment shipment = shipment(sea, por, false);

        List<Quote> quotes = service.quoteAll(shipment);

        assertThat(quotes).hasSize(3);
        assertThat(quotes).extracting(Quote::mode)
                .containsExactlyInAnyOrder(TransportType.TRUCK, TransportType.RAIL, TransportType.BOAT);
        assertThat(quotes).isSortedAccordingTo((a, b) -> a.price().compareTo(b.price()));
        assertThat(quotes).allSatisfy(quote -> assertThat(quote.eta()).isPositive());
    }

    @Test
    void modoInelegivelNaoEntraNaCotacao() {
        QuoteService service = new QuoteService(modes, distanceBook, fixedProvider(baseRates()));
        Shipment toInland = shipment(sea, inland, false);

        List<Quote> quotes = service.quoteAll(toInland);

        assertThat(quotes).extracting(Quote::mode).containsExactly(TransportType.TRUCK);
        assertThat(service.rejectionsFor(toInland)).containsOnlyKeys(TransportType.RAIL, TransportType.BOAT);
    }

    @Test
    void mudancaDeTarifaEntreDuasCotacoesMudaOPreco() {
        QuoteService service = new QuoteService(modes, distanceBook, fixedProvider(baseRates()));
        Shipment shipment = shipment(sea, por, false);

        BigDecimal truckBefore = truckPrice(service.quoteAll(shipment, snapshot(baseRates())));
        Map<TransportType, ModeRates> raised = baseRates();
        raised.put(TransportType.TRUCK, withBaseFare(raised.get(TransportType.TRUCK), new BigDecimal("500")));
        BigDecimal truckAfter = truckPrice(service.quoteAll(shipment, snapshot(raised)));

        assertThat(truckAfter).isGreaterThan(truckBefore);
    }

    @Test
    void mesmaCargaEMesmoSnapshotProduzemAMesmaCotacao() {
        QuoteService service = new QuoteService(modes, distanceBook, fixedProvider(baseRates()));
        Shipment shipment = shipment(sea, por, false);
        RateSnapshot snapshot = snapshot(baseRates());

        assertThat(service.quoteAll(shipment, snapshot)).isEqualTo(service.quoteAll(shipment, snapshot));
    }

    private BigDecimal truckPrice(List<Quote> quotes) {
        return quotes.stream()
                .filter(quote -> quote.mode() == TransportType.TRUCK)
                .findFirst()
                .orElseThrow()
                .price();
    }

    private Shipment shipment(Location origin, Location destination, boolean hazardous) {
        return new Shipment(
                Dimensions.ofCentimeters("120", "100", "100"),
                Weight.ofKilograms("800"),
                new BigDecimal("5000"),
                origin,
                destination,
                hazardous);
    }

    private RateProvider fixedProvider(Map<TransportType, ModeRates> rates) {
        RateSnapshot snapshot = snapshot(rates);
        return () -> snapshot;
    }

    private RateSnapshot snapshot(Map<TransportType, ModeRates> rates) {
        return new RateSnapshot(Instant.parse("2026-06-01T00:00:00Z"), rates);
    }

    private Map<TransportType, ModeRates> baseRates() {
        Map<TransportType, ModeRates> rates = new EnumMap<>(TransportType.class);
        rates.put(TransportType.TRUCK, new ModeRates(
                new BigDecimal("80"), new BigDecimal("25"), new BigDecimal("0.40"),
                new BigDecimal("0.90"), new BigDecimal("12"), new BigDecimal("150")));
        rates.put(TransportType.RAIL, new ModeRates(
                new BigDecimal("120"), new BigDecimal("18"), new BigDecimal("0.22"),
                new BigDecimal("0.45"), new BigDecimal("7"), new BigDecimal("90")));
        rates.put(TransportType.BOAT, new ModeRates(
                new BigDecimal("200"), new BigDecimal("9"), new BigDecimal("0.10"),
                new BigDecimal("0.20"), new BigDecimal("5"), new BigDecimal("300")));
        return rates;
    }

    private ModeRates withBaseFare(ModeRates rates, BigDecimal baseFare) {
        return new ModeRates(baseFare, rates.perCubicMeter(), rates.perKilogram(),
                rates.perKilometer(), rates.fuelSurchargePercent(), rates.hazardSurcharge());
    }
}
