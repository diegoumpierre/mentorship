package com.poc.flink.service;

import com.poc.flink.config.FlinkConfig;
import com.poc.flink.config.KafkaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class FlinkJobServiceTest {

    private FlinkConfig flinkConfig;
    private KafkaConfig kafkaConfig;
    private FlinkJobService flinkJobService;

    @BeforeEach
    void setUp() throws Exception {
        flinkConfig = new FlinkConfig();
        kafkaConfig = new KafkaConfig();
        setField(flinkConfig, "restUrl", "http://localhost:99999");
        setField(flinkConfig, "jarPath", "target/nonexistent.jar");
        setField(kafkaConfig, "bootstrapServers", "localhost:9092");
        setField(kafkaConfig, "inputTopic", "words-input");
        setField(kafkaConfig, "outputTopic", "words-output");
        setField(kafkaConfig, "groupId", "flink-word-count");
        setField(kafkaConfig, "transactionsTopic", "transactions-input");
        setField(kafkaConfig, "fraudAlertsTopic", "fraud-alerts");
        setField(kafkaConfig, "userEventsTopic", "user-events");
        setField(kafkaConfig, "sessionSummariesTopic", "session-summaries");
        setField(kafkaConfig, "rawEventsTopic", "raw-events");
        setField(kafkaConfig, "validEventsTopic", "valid-events");
        setField(kafkaConfig, "deadLetterTopic", "dead-letter-queue");
        flinkJobService = new FlinkJobService(flinkConfig, kafkaConfig);
    }

    @Test
    void runWordCountLocallyReturnsCompleted() {
        String result = flinkJobService.runWordCountLocally("hello world hello");
        assertThat(result).isEqualTo("Job completed");
    }

    @Test
    void runWindowedWordCountLocallyReturnsCompleted() {
        String result = flinkJobService.runWindowedWordCountLocally("hello world hello flink", 10);
        assertThat(result).isEqualTo("Job completed");
    }

    @Test
    void submitWordCountJobFailsWithoutJar() {
        String result = flinkJobService.submitWordCountJob("hello");
        assertThat(result).startsWith("Job failed");
    }

    @Test
    void submitWindowedWordCountJobFailsWithoutJar() {
        String result = flinkJobService.submitWindowedWordCountJob("hello", 10);
        assertThat(result).startsWith("Job failed");
    }

    @Test
    void submitKafkaWordCountJobFailsWithoutJar() {
        String result = flinkJobService.submitKafkaWordCountJob(10);
        assertThat(result).startsWith("Kafka job submission failed");
    }

    @Test
    void submitEventValidationJobFailsWithoutJar() {
        String result = flinkJobService.submitEventValidationJob();
        assertThat(result).startsWith("Event Validation job submission failed");
    }

    @Test
    void submitFraudDetectionJobFailsWithoutJar() {
        String result = flinkJobService.submitFraudDetectionJob();
        assertThat(result).startsWith("Fraud Detection job submission failed");
    }

    @Test
    void submitUserSessionJobFailsWithoutJar() {
        String result = flinkJobService.submitUserSessionJob();
        assertThat(result).startsWith("User Session job submission failed");
    }

    @Test
    void getClusterStatusReturnsErrorWithoutCluster() {
        var result = flinkJobService.getClusterStatus();
        assertThat(result).containsKey("error");
    }

    @Test
    void listJobsReturnsErrorWithoutCluster() {
        var result = flinkJobService.listJobs();
        assertThat(result).containsKey("error");
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
