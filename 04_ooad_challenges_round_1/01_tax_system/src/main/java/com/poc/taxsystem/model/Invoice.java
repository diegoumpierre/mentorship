package com.poc.taxsystem.model;

import java.time.LocalDate;
import java.util.List;

public record Invoice(State state, LocalDate date, List<LineItem> items) {
}
