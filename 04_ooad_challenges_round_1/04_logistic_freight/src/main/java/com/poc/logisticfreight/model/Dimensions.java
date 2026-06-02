package com.poc.logisticfreight.model;

import java.math.BigDecimal;
import java.math.MathContext;

public record Dimensions(BigDecimal lengthCm, BigDecimal widthCm, BigDecimal heightCm) {

    private static final BigDecimal CUBIC_CM_PER_CUBIC_M = new BigDecimal("1000000");

    public Dimensions {
        requirePositive("length", lengthCm);
        requirePositive("width", widthCm);
        requirePositive("height", heightCm);
    }

    public static Dimensions ofCentimeters(String lengthCm, String widthCm, String heightCm) {
        return new Dimensions(new BigDecimal(lengthCm), new BigDecimal(widthCm), new BigDecimal(heightCm));
    }

    public Volume volume() {
        BigDecimal cubicCentimeters = lengthCm.multiply(widthCm).multiply(heightCm);
        return new Volume(cubicCentimeters.divide(CUBIC_CM_PER_CUBIC_M, MathContext.DECIMAL64));
    }

    public BigDecimal longestSideCm() {
        return lengthCm.max(widthCm).max(heightCm);
    }

    private static void requirePositive(String side, BigDecimal value) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(side + " must be greater than zero: " + value);
        }
    }
}
