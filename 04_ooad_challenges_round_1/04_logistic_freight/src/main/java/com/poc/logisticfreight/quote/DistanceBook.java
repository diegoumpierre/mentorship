package com.poc.logisticfreight.quote;

import com.poc.logisticfreight.model.Location;

import java.math.BigDecimal;

public interface DistanceBook {

    BigDecimal distanceKm(Location origin, Location destination);
}
