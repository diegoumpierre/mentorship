package com.poc.flink.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionSummary {

    private String userId;
    private long totalEvents;
    private Map<String, Long> actionCounts;
    private long sessionStartTime;
    private long lastEventTime;
}
