package com.poc.logisticfreight.pricing;

import java.math.BigDecimal;

public interface PriceComponent {

    String name();

    BigDecimal amount(PricingContext context);
}
