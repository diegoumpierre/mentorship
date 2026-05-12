package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;

import java.time.LocalDate;

// Lancada quando nao existe rate vigente pra (product, state) na data.
// Cobre tres casos: rate inexistente, data antes do primeiro periodo,
// e data caindo num gap entre periodos cadastrados.
public class NoApplicableRateException extends RuntimeException {

    public NoApplicableRateException(Product product, State state, LocalDate when) {
        super("Sem tax rate vigente pra product=" + product.getId()
                + " state=" + state.getCode() + " em " + when);
    }
}
