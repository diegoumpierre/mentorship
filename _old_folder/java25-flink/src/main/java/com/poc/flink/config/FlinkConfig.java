package com.poc.flink.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
@Getter
public class FlinkConfig {

    @Value("${flink.jobmanager.host}")
    private String jobManagerHost;

    @Value("${flink.jobmanager.port}")
    private int jobManagerPort;

    @Value("${flink.rest.url}")
    private String restUrl;

    @Value("${flink.jar.path}")
    private String jarPath;




}
