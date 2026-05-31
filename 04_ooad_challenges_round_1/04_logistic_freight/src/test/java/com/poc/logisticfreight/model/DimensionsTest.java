package com.poc.logisticfreight.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DimensionsTest {

    @Test
    void converteCentimetrosCubicosEmMetrosCubicos() {
        Dimensions oneCubicMeter = Dimensions.ofCentimeters("100", "100", "100");

        assertThat(oneCubicMeter.volume().cubicMeters()).isEqualByComparingTo("1");
    }

    @Test
    void maiorLadoVemDeQualquerEixo() {
        Dimensions box = Dimensions.ofCentimeters("40", "260", "30");

        assertThat(box.longestSideCm()).isEqualByComparingTo("260");
    }

    @Test
    void rejeitaLadoZeroOuNegativo() {
        assertThatThrownBy(() -> Dimensions.ofCentimeters("0", "10", "10"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Dimensions.ofCentimeters("10", "-5", "10"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
