package com.poc.taxsystem.model;

import java.math.BigDecimal;

public record InvoiceTotal(BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
}
