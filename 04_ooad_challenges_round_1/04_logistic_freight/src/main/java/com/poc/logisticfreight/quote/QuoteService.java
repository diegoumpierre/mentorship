package com.poc.logisticfreight.quote;

import com.poc.logisticfreight.mode.TransportMode;
import com.poc.logisticfreight.model.Shipment;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.rates.RateProvider;
import com.poc.logisticfreight.rates.RateSnapshot;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuoteService {

    private final List<TransportMode> modes;
    private final DistanceBook distanceBook;
    private final RateProvider rateProvider;

    public QuoteService(List<TransportMode> modes, DistanceBook distanceBook, RateProvider rateProvider) {
        this.modes = List.copyOf(modes);
        this.distanceBook = distanceBook;
        this.rateProvider = rateProvider;
    }

    public List<Quote> quoteAll(Shipment shipment) {
        return quoteAll(shipment, rateProvider.currentSnapshot());
    }

    public List<Quote> quoteAll(Shipment shipment, RateSnapshot snapshot) {
        BigDecimal distanceKm = distanceBook.distanceKm(shipment.origin(), shipment.destination());
        return modes.stream()
                .filter(mode -> mode.isEligibleFor(shipment))
                .map(mode -> new Quote(
                        mode.type(),
                        mode.pricingStrategy().price(shipment, distanceKm, snapshot),
                        mode.eta(distanceKm)))
                .sorted(Comparator.comparing(Quote::price))
                .toList();
    }

    public Map<TransportType, List<String>> rejectionsFor(Shipment shipment) {
        Map<TransportType, List<String>> rejections = new LinkedHashMap<>();
        for (TransportMode mode : modes) {
            List<String> reasons = mode.rejectionReasons(shipment);
            if (!reasons.isEmpty()) {
                rejections.put(mode.type(), reasons);
            }
        }
        return rejections;
    }
}
