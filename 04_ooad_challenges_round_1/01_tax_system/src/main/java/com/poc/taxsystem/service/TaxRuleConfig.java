package com.poc.taxsystem.service;

import com.poc.taxsystem.repository.TaxRateRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaxRuleConfig {

    @Bean
    public JpaTaxRuleProvider jpaTaxRuleProvider(TaxRateRepository rates) {
        return new JpaTaxRuleProvider(rates);
    }
}
