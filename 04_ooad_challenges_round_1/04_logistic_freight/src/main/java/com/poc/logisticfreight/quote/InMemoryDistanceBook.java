package com.poc.logisticfreight.quote;

import com.poc.logisticfreight.model.Location;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class InMemoryDistanceBook implements DistanceBook {

    private final Map<String, BigDecimal> distancesByRoute = new HashMap<>();

    public InMemoryDistanceBook put(String originCode, String destinationCode, BigDecimal distanceKm) {
        distancesByRoute.put(routeKey(originCode, destinationCode), distanceKm);
        distancesByRoute.put(routeKey(destinationCode, originCode), distanceKm);
        return this;
    }

    @Override
    public BigDecimal distanceKm(Location origin, Location destination) {
        BigDecimal distance = distancesByRoute.get(routeKey(origin.code(), destination.code()));
        if (distance == null) {
            throw new IllegalStateException("no known distance between " + origin.code() + " and " + destination.code());
        }
        return distance;
    }

    private static String routeKey(String originCode, String destinationCode) {
        return originCode + "->" + destinationCode;
    }
}
