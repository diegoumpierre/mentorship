package com.poc.logisticfreight.quote;

import com.poc.logisticfreight.mode.TransportMode;
import com.poc.logisticfreight.model.Dimensions;
import com.poc.logisticfreight.model.Location;
import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.model.Weight;
import com.poc.logisticfreight.rates.ModeRates;
import com.poc.logisticfreight.rates.RateSnapshot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OpenClosedTest {

    @Test
    void modoNovoEntraNaCotacaoSemMexerNoQuoteService() {
        Location origin = new Location("SEA", Set.of(TransportType.TRUCK, TransportType.AIRPLANE));
        Location destination = new Location("SLC", Set.of(TransportType.TRUCK, TransportType.AIRPLANE));

        List<TransportMode> withAirplane = List.of(TestModes.truck(), TestModes.airplane());
        DistanceBook distanceBook = new InMemoryDistanceBook().put("SEA", "SLC", new BigDecimal("1310"));
        QuoteService service = new QuoteService(withAirplane, distanceBook, () -> snapshot());

        Shipment shipment = new Shipment(
                Dimensions.ofCentimeters("100", "100", "100"),
                Weight.ofKilograms("800"),
                new BigDecimal("5000"),
                origin,
                destination,
                false);

        List<Quote> quotes = service.quoteAll(shipment);

        assertThat(quotes).extracting(Quote::mode)
                .containsExactlyInAnyOrder(TransportType.TRUCK, TransportType.AIRPLANE);
        Quote air = quotes.stream().filter(quote -> quote.mode() == TransportType.AIRPLANE).findFirst().orElseThrow();
        assertThat(air.price()).isPositive();
        assertThat(air.eta()).isPositive();
    }

    private RateSnapshot snapshot() {
        Map<TransportType, ModeRates> rates = new EnumMap<>(TransportType.class);
        rates.put(TransportType.TRUCK, new ModeRates(
                new BigDecimal("80"), new BigDecimal("25"), new BigDecimal("0.40"),
                new BigDecimal("0.90"), new BigDecimal("12"), new BigDecimal("150")));
        rates.put(TransportType.AIRPLANE, new ModeRates(
                new BigDecimal("350"), new BigDecimal("60"), new BigDecimal("3.50"),
                new BigDecimal("1.80"), new BigDecimal("20"), new BigDecimal("600")));
        return new RateSnapshot(Instant.parse("2026-06-01T00:00:00Z"), rates);
    }
}
