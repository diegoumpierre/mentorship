package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;

import java.time.LocalDate;

// Lancada quando mais de uma rate vigente bate pra (product, state, data) -
// indica periodos sobrepostos cadastrados, o que e um bug de dados.
public class AmbiguousRateException extends RuntimeException {

    public AmbiguousRateException(Product product, State state, LocalDate when) {
        super("Mais de uma tax rate vigente pra product=" + product.getId()
                + " state=" + state.getCode() + " em " + when);
    }
}
