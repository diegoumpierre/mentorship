package com.poc.taxsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class EffectivePeriod {

    @Column(name = "effective_from")
    private LocalDate from;

    @Column(name = "effective_to")
    private LocalDate to;

    protected EffectivePeriod() {
    }

    public EffectivePeriod(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    // Half-open interval [from, to): includes "from", excludes "to".
    // "to" null = open-ended, vigente até nova versão substituir.
    public boolean contains(LocalDate when) {
        if (when.isBefore(from)) {
            return false;
        }
        return to == null || when.isBefore(to);
    }
}
