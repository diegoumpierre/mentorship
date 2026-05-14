package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Invoice;
import com.poc.taxsystem.model.InvoiceTotal;
import com.poc.taxsystem.model.LineItem;
import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
import com.poc.taxsystem.model.TaxRate;
import com.poc.taxsystem.repository.TaxRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaxEngine {

    static final RoundingMode TAX_ROUNDING = RoundingMode.HALF_UP;
    static final int MONEY_SCALE = 2;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final TaxRateRepository rates;

    public TaxEngine(TaxRateRepository rates) {
        this.rates = rates;
    }

    public BigDecimal taxFor(Product product, State state, LocalDate when) {
        BigDecimal percent = findRate(product, state, when).getPercent();
        return computeTax(product.getPrice(), percent);
    }

    public InvoiceTotal totalFor(Invoice invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        for (LineItem item : invoice.items()) {
            BigDecimal lineSubtotal = item.product().getPrice()
                    .multiply(BigDecimal.valueOf(item.quantity()));
            BigDecimal lineRate = findRate(item.product(), invoice.state(), invoice.date()).getPercent();
            BigDecimal lineTax = computeTax(lineSubtotal, lineRate);
            subtotal = subtotal.add(lineSubtotal);
            totalTax = totalTax.add(lineTax);
        }
        BigDecimal subRounded = subtotal.setScale(MONEY_SCALE, TAX_ROUNDING);
        BigDecimal totalRounded = subRounded.add(totalTax).setScale(MONEY_SCALE, TAX_ROUNDING);
        return new InvoiceTotal(subRounded, totalTax, totalRounded);
    }

    private BigDecimal computeTax(BigDecimal base, BigDecimal percent) {
        return base.multiply(percent).divide(HUNDRED, MONEY_SCALE, TAX_ROUNDING);
    }

    private TaxRate findRate(Product product, State state, LocalDate when) {
        List<TaxRate> applicable = rates
                .findByProductIdAndStateCode(product.getId(), state.getCode())
                .stream()
                .filter(r -> r.getPeriod().contains(when))
                .toList();
        if (applicable.isEmpty()) {
            throw new IllegalStateException("Sem tax rate vigente pra product=" + product.getId()
                    + " state=" + state.getCode() + " em " + when);
        }
        if (applicable.size() > 1) {
            throw new IllegalStateException("Mais de uma tax rate vigente pra product=" + product.getId()
                    + " state=" + state.getCode() + " em " + when);
        }
        return applicable.get(0);
    }
}
