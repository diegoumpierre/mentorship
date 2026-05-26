package com.poc.taxsystem.service;

import com.poc.taxsystem.model.Product;
import com.poc.taxsystem.model.State;

import java.time.LocalDate;
import java.util.List;

public interface TaxRuleProvider {

    List<TaxRule> rulesFor(Product product, State state, LocalDate when);
}
