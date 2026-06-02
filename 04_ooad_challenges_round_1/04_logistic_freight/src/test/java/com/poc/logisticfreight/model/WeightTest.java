package com.poc.logisticfreight.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeightTest {

    @Test
    void rejeitaPesoNegativo() {
        assertThatThrownBy(() -> Weight.ofKilograms("-1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void pesoZeroEhValido() {
        assertThat(Weight.ofKilograms("0").kilograms()).isEqualByComparingTo("0");
    }

    @Test
    void comparaPesosPorValor() {
        Weight heavier = Weight.ofKilograms("100.5");
        Weight lighter = Weight.ofKilograms("100.4");

        assertThat(heavier.isHeavierThan(lighter)).isTrue();
        assertThat(lighter.isHeavierThan(heavier)).isFalse();
    }
}
