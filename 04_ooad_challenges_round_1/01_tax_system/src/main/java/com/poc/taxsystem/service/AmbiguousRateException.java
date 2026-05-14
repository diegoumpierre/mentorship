package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;

import java.time.LocalDate;


public class AmbiguousRateException extends RuntimeException {

    public AmbiguousRateException(Product product, State state, LocalDate when) {
        super("Mais de uma tax rate vigente pra product=" + product.getId()
                + " state=" + state.getCode() + " em " + when);
    }
}
