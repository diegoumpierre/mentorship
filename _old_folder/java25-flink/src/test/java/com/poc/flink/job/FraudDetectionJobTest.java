package com.poc.flink.job;

import com.poc.flink.model.Transaction;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.junit5.MiniClusterExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FraudDetectionJobTest {

    @RegisterExtension
    static final MiniClusterExtension MINI_CLUSTER = new MiniClusterExtension(
            new MiniClusterResourceConfiguration.Builder()
                    .setNumberTaskManagers(1)
                    .setNumberSlotsPerTaskManager(2)
                    .build()
    );

    @Test
    void parseTransactionFromJson() {
        String json = "{\"userId\":\"user1\",\"city\":\"New York\",\"amount\":150.50,\"timestamp\":1700000000000}";

        Transaction tx = FraudDetectionJob.parseTransaction(json);

        assertThat(tx.getUserId()).isEqualTo("user1");
        assertThat(tx.getCity()).isEqualTo("New York");
        assertThat(tx.getAmount()).isEqualTo(150.50);
        assertThat(tx.getTimestamp()).isEqualTo(1700000000000L);
    }

    @Test
    void parseTransactionFailsOnMissingField() {
        String json = "{\"userId\":\"user1\",\"city\":\"NYC\"}";

        assertThatThrownBy(() -> FraudDetectionJob.parseTransaction(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount");
    }

    @Test
    void detectFraudTriggersAlertFor3DifferentCities() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = System.currentTimeMillis();

        DataStream<Transaction> transactions = env.fromData(
                Transaction.builder().userId("user1").city("New York").amount(100.0).timestamp(now).build(),
                Transaction.builder().userId("user1").city("London").amount(200.0).timestamp(now + 60_000).build(),
                Transaction.builder().userId("user1").city("Tokyo").amount(300.0).timestamp(now + 120_000).build()
        );

        DataStream<String> alerts = FraudDetectionJob.detectFraud(transactions);

        var result = alerts.executeAndCollect(10);

        assertThat(result).hasSize(1);
        String alert = result.get(0);
        assertThat(alert).contains("user1");
        assertThat(alert).contains("New York");
        assertThat(alert).contains("London");
        assertThat(alert).contains("Tokyo");
        assertThat(alert).contains("3 purchases in 3 different cities within 5 minutes");
    }

    @Test
    void detectFraudDoesNotTriggerForSameCity() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = System.currentTimeMillis();

        DataStream<Transaction> transactions = env.fromData(
                Transaction.builder().userId("user1").city("New York").amount(100.0).timestamp(now).build(),
                Transaction.builder().userId("user1").city("New York").amount(200.0).timestamp(now + 60_000).build(),
                Transaction.builder().userId("user1").city("New York").amount(300.0).timestamp(now + 120_000).build()
        );

        DataStream<String> alerts = FraudDetectionJob.detectFraud(transactions);

        var result = alerts.executeAndCollect(10);

        assertThat(result).isEmpty();
    }

    @Test
    void detectFraudDoesNotTriggerForOnly2DifferentCities() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = System.currentTimeMillis();

        DataStream<Transaction> transactions = env.fromData(
                Transaction.builder().userId("user1").city("New York").amount(100.0).timestamp(now).build(),
                Transaction.builder().userId("user1").city("London").amount(200.0).timestamp(now + 60_000).build(),
                Transaction.builder().userId("user1").city("London").amount(300.0).timestamp(now + 120_000).build()
        );

        DataStream<String> alerts = FraudDetectionJob.detectFraud(transactions);

        var result = alerts.executeAndCollect(10);

        assertThat(result).isEmpty();
    }

    @Test
    void detectFraudIsolatesUsersByKey() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = System.currentTimeMillis();

        // Two different users each make purchases — neither hits 3 different cities alone
        DataStream<Transaction> transactions = env.fromData(
                Transaction.builder().userId("user1").city("New York").amount(100.0).timestamp(now).build(),
                Transaction.builder().userId("user2").city("Paris").amount(150.0).timestamp(now + 10_000).build(),
                Transaction.builder().userId("user1").city("London").amount(200.0).timestamp(now + 60_000).build(),
                Transaction.builder().userId("user2").city("Berlin").amount(250.0).timestamp(now + 70_000).build()
        );

        DataStream<String> alerts = FraudDetectionJob.detectFraud(transactions);

        var result = alerts.executeAndCollect(10);

        assertThat(result).isEmpty();
    }
}
