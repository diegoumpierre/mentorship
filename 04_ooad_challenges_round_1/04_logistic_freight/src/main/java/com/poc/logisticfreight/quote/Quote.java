package com.poc.logisticfreight.quote;

import com.poc.logisticfreight.model.TransportType;

import java.math.BigDecimal;
import java.time.Duration;

public record Quote(TransportType mode, BigDecimal price, Duration eta) {
}
