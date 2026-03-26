package com.poc.flink.job;

import com.poc.flink.model.FraudAlert;
import com.poc.flink.model.Transaction;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fraud Detection using Flink CEP (Complex Event Processing).
 *
 * Pattern: If a user makes 3 purchases in different cities within 5 minutes, trigger a fraud alert.
 *
 * Input: JSON transactions from Kafka topic "transactions-input"
 *   Format: {"userId":"user1","city":"New York","amount":100.0,"timestamp":1700000000000}
 *
 * Output: Fraud alerts to Kafka topic "fraud-alerts"
 */
public class FraudDetectionJob {

    public static void main(String[] args) throws Exception {
        String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        String inputTopic = args.length > 1 ? args[1] : "transactions-input";
        String outputTopic = args.length > 2 ? args[2] : "fraud-alerts";
        String groupId = args.length > 3 ? args[3] : "fraud-detection";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        execute(env, bootstrapServers, inputTopic, outputTopic, groupId);
    }

    public static void execute(StreamExecutionEnvironment env,
                               String bootstrapServers,
                               String inputTopic,
                               String outputTopic,
                               String groupId) throws Exception {

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(inputTopic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.builder()
                                .setTopic(outputTopic)
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()
                )
                .build();

        DataStream<String> rawStream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Transactions Source"
        );

        DataStream<Transaction> transactions = rawStream.map(FraudDetectionJob::parseTransaction);

        DataStream<String> alerts = detectFraud(transactions);

        alerts.sinkTo(sink);
        alerts.print("FRAUD ALERT");

        env.execute("Fraud Detection CEP Job");
    }

    /**
     * Core CEP logic — can be used with any DataStream<Transaction>, making it testable
     * without Kafka.
     */
    public static DataStream<String> detectFraud(DataStream<Transaction> transactions) {

        DataStream<Transaction> keyedTransactions = transactions
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Transaction>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                                .withTimestampAssigner((tx, ts) -> tx.getTimestamp())
                );

        // CEP Pattern: 3 purchases in different cities within 5 minutes
        Pattern<Transaction, ?> fraudPattern = Pattern.<Transaction>begin("first")
                .next("second")
                .where(new IterativeCondition<>() {
                    @Override
                    public boolean filter(Transaction second, Context<Transaction> ctx) throws Exception {
                        for (Transaction first : ctx.getEventsForPattern("first")) {
                            if (!second.getCity().equals(first.getCity())) {
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .next("third")
                .where(new IterativeCondition<>() {
                    @Override
                    public boolean filter(Transaction third, Context<Transaction> ctx) throws Exception {
                        boolean diffFromFirst = false;
                        boolean diffFromSecond = false;

                        for (Transaction first : ctx.getEventsForPattern("first")) {
                            if (!third.getCity().equals(first.getCity())) {
                                diffFromFirst = true;
                            }
                        }
                        for (Transaction second : ctx.getEventsForPattern("second")) {
                            if (!third.getCity().equals(second.getCity())) {
                                diffFromSecond = true;
                            }
                        }
                        return diffFromFirst && diffFromSecond;
                    }
                })
                .within(Duration.ofMinutes(5));

        PatternStream<Transaction> patternStream = CEP.pattern(
                keyedTransactions.keyBy(Transaction::getUserId),
                fraudPattern
        );

        return patternStream.select(new PatternSelectFunction<>() {
            @Override
            public String select(Map<String, List<Transaction>> pattern) {
                Transaction first = pattern.get("first").get(0);
                Transaction second = pattern.get("second").get(0);
                Transaction third = pattern.get("third").get(0);

                FraudAlert alert = FraudAlert.builder()
                        .userId(first.getUserId())
                        .cities(List.of(first.getCity(), second.getCity(), third.getCity()))
                        .amounts(List.of(first.getAmount(), second.getAmount(), third.getAmount()))
                        .firstTransactionTime(first.getTimestamp())
                        .lastTransactionTime(third.getTimestamp())
                        .reason("3 purchases in 3 different cities within 5 minutes")
                        .build();

                return formatAlert(alert);
            }
        });
    }

    static Transaction parseTransaction(String json) {
        // Simple JSON parsing without external dependencies
        String userId = extractJsonField(json, "userId");
        String city = extractJsonField(json, "city");
        double amount = Double.parseDouble(extractJsonField(json, "amount"));
        long timestamp = Long.parseLong(extractJsonField(json, "timestamp"));

        return Transaction.builder()
                .userId(userId)
                .city(city)
                .amount(amount)
                .timestamp(timestamp)
                .build();
    }

    private static String extractJsonField(String json, String field) {
        String searchKey = "\"" + field + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) {
            throw new IllegalArgumentException("Field '" + field + "' not found in JSON: " + json);
        }
        int colonIndex = json.indexOf(':', keyIndex + searchKey.length());
        int valueStart = colonIndex + 1;

        // Skip whitespace
        while (valueStart < json.length() && json.charAt(valueStart) == ' ') {
            valueStart++;
        }

        if (json.charAt(valueStart) == '"') {
            // String value
            int valueEnd = json.indexOf('"', valueStart + 1);
            return json.substring(valueStart + 1, valueEnd);
        } else {
            // Numeric value
            int valueEnd = valueStart;
            while (valueEnd < json.length() && json.charAt(valueEnd) != ',' && json.charAt(valueEnd) != '}') {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd).trim();
        }
    }

    private static String formatAlert(FraudAlert alert) {
        return "{\"userId\":\"" + alert.getUserId() + "\""
                + ",\"cities\":" + formatList(alert.getCities())
                + ",\"amounts\":" + alert.getAmounts()
                + ",\"firstTransactionTime\":" + alert.getFirstTransactionTime()
                + ",\"lastTransactionTime\":" + alert.getLastTransactionTime()
                + ",\"reason\":\"" + alert.getReason() + "\"}";
    }

    private static String formatList(List<String> items) {
        List<String> quoted = new ArrayList<>();
        for (String item : items) {
            quoted.add("\"" + item + "\"");
        }
        return "[" + String.join(",", quoted) + "]";
    }
}
