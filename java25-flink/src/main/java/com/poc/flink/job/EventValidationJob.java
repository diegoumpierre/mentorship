package com.poc.flink.job;

import com.poc.flink.model.UserEvent;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;

public class EventValidationJob {

    static final OutputTag<String> INVALID_EVENTS = new OutputTag<>("invalid-events") {};

    private static final List<String> VALID_ACTIONS = List.of("login", "click", "purchase", "logout");

    public static void main(String[] args) throws Exception {
        String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        String inputTopic = args.length > 1 ? args[1] : "raw-events";
        String validTopic = args.length > 2 ? args[2] : "valid-events";
        String invalidTopic = args.length > 3 ? args[3] : "dead-letter-queue";
        String groupId = args.length > 4 ? args[4] : "event-validation";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        execute(env, bootstrapServers, inputTopic, validTopic, invalidTopic, groupId);
    }

    public static void execute(StreamExecutionEnvironment env,
                               String bootstrapServers,
                               String inputTopic,
                               String validTopic,
                               String invalidTopic,
                               String groupId) throws Exception {

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(inputTopic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        KafkaSink<String> validSink = buildSink(bootstrapServers, validTopic);
        KafkaSink<String> invalidSink = buildSink(bootstrapServers, invalidTopic);

        DataStream<String> rawStream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Raw Events Source"
        );

        SplitResult split = splitEvents(rawStream);

        split.valid().sinkTo(validSink);
        split.valid().print("VALID");

        split.invalid().sinkTo(invalidSink);
        split.invalid().print("INVALID");

        env.execute("Event Validation Job");
    }

    public static SplitResult splitEvents(DataStream<String> rawStream) {
        SingleOutputStreamOperator<String> mainStream = rawStream
                .process(new EventRouter());

        DataStream<String> validEvents = mainStream;
        DataStream<String> invalidEvents = mainStream.getSideOutput(INVALID_EVENTS);

        return new SplitResult(validEvents, invalidEvents);
    }

    static class EventRouter extends ProcessFunction<String, String> {

        @Override
        public void processElement(String json,
                                   ProcessFunction<String, String>.Context ctx,
                                   Collector<String> out) {
            ValidationResult result = validate(json);

            if (result.isValid) {
                out.collect(json);
            } else {
                ctx.output(INVALID_EVENTS,
                        "{\"original\":" + json + ",\"error\":\"" + result.reason + "\"}");
            }
        }
    }

    static ValidationResult validate(String json) {
        if (json == null || json.isBlank()) {
            return ValidationResult.fail("empty input");
        }

        if (!json.trim().startsWith("{") || !json.trim().endsWith("}")) {
            return ValidationResult.fail("not valid JSON");
        }

        String userId;
        String action;
        String timestampStr;
        try {
            userId = extractJsonField(json, "userId");
            action = extractJsonField(json, "action");
            timestampStr = extractJsonField(json, "timestamp");
        } catch (IllegalArgumentException e) {
            return ValidationResult.fail("missing field: " + e.getMessage());
        }

        if (userId.isBlank()) {
            return ValidationResult.fail("userId is blank");
        }

        if (!VALID_ACTIONS.contains(action)) {
            return ValidationResult.fail("unknown action: " + action);
        }

        try {
            long ts = Long.parseLong(timestampStr);
            if (ts <= 0) {
                return ValidationResult.fail("timestamp must be positive");
            }
        } catch (NumberFormatException e) {
            return ValidationResult.fail("timestamp is not a number");
        }

        return ValidationResult.ok();
    }

    record ValidationResult(boolean isValid, String reason) {
        static ValidationResult ok() { return new ValidationResult(true, null); }
        static ValidationResult fail(String reason) { return new ValidationResult(false, reason); }
    }

    public record SplitResult(DataStream<String> valid, DataStream<String> invalid) {}

    private static String extractJsonField(String json, String field) {
        String searchKey = "\"" + field + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) {
            throw new IllegalArgumentException(field);
        }
        int colonIndex = json.indexOf(':', keyIndex + searchKey.length());
        int valueStart = colonIndex + 1;

        while (valueStart < json.length() && json.charAt(valueStart) == ' ') {
            valueStart++;
        }

        if (json.charAt(valueStart) == '"') {
            int valueEnd = json.indexOf('"', valueStart + 1);
            return json.substring(valueStart + 1, valueEnd);
        } else {
            int valueEnd = valueStart;
            while (valueEnd < json.length() && json.charAt(valueEnd) != ',' && json.charAt(valueEnd) != '}') {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd).trim();
        }
    }

    private static KafkaSink<String> buildSink(String bootstrapServers, String topic) {
        return KafkaSink.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.builder()
                                .setTopic(topic)
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()
                )
                .build();
    }
}
