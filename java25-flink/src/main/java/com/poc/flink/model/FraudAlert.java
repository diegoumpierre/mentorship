package com.poc.flink.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert {

    private String userId;
    private List<String> cities;
    private List<Double> amounts;
    private long firstTransactionTime;
    private long lastTransactionTime;
    private String reason;
}
