package com.poc.logisticfreight.model;

import java.util.Set;

public record Location(String code, Set<TransportType> servedBy) {

    public Location {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("location code is required");
        }
        servedBy = servedBy == null ? Set.of() : Set.copyOf(servedBy);
    }

    public boolean isServedBy(TransportType type) {
        return servedBy.contains(type);
    }
}
