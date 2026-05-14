package com.poc.taxsystem.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_tax_rate")
public class TaxRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "state_code")
    private State state;

    private BigDecimal percent;

    @Embedded
    private EffectivePeriod period;

    protected TaxRate() {
    }

    public TaxRate(Product product, State state, BigDecimal percent, EffectivePeriod period) {
        this.product = product;
        this.state = state;
        this.percent = percent;
        this.period = period;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public State getState() {
        return state;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public EffectivePeriod getPeriod() {
        return period;
    }
}
