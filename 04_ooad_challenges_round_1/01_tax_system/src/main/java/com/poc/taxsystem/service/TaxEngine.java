package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Invoice;
import com.poc.taxsystem.model.InvoiceTotal;
import com.poc.taxsystem.model.LineItem;
import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;
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

    private final List<CompoundTaxLayer> layers;

    public TaxEngine(List<CompoundTaxLayer> layers) {
        this.layers = layers;
    }

    public BigDecimal taxFor(Product product, State state, LocalDate when) {
        BigDecimal totalPercent = totalPercentFor(product, state, when);
        return computeTax(product.getPrice(), totalPercent);
    }

    public InvoiceTotal totalFor(Invoice invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        for (LineItem item : invoice.items()) {
            BigDecimal lineSubtotal = item.product().getPrice()
                    .multiply(BigDecimal.valueOf(item.quantity()));
            BigDecimal linePercent = totalPercentFor(item.product(), invoice.state(), invoice.date());
            BigDecimal lineTax = computeTax(lineSubtotal, linePercent);
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

    private BigDecimal totalPercentFor(Product product, State state, LocalDate when) {
        BigDecimal total = BigDecimal.ZERO;
        int matched = 0;
        for (CompoundTaxLayer layer : layers) {
            List<TaxRule> rules = layer.rulesFor(product, state, when);
            if (rules.size() > 1) {
                throw new IllegalStateException("Mais de uma tax rate vigente pra product=" + product.getId()
                        + " state=" + state.getCode() + " em " + when);
            }
            if (rules.size() == 1) {
                total = total.add(rules.get(0).percent());
                matched++;
            }
        }
        if (matched == 0) {
            throw new IllegalStateException("Sem tax rate vigente pra product=" + product.getId()
                    + " state=" + state.getCode() + " em " + when);
        }
        return total;
    }
}
