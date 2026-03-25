package com.poc.flink.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
@Getter
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.input-topic}")
    private String inputTopic;

    @Value("${kafka.output-topic}")
    private String outputTopic;

    @Value("${kafka.group-id}")
    private String groupId;
}