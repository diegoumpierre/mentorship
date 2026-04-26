package com.poc.flink.job;

import com.poc.flink.model.SessionSummary;
import com.poc.flink.model.UserEvent;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class UserSessionJob {

    public static void main(String[] args) throws Exception {
        String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        String inputTopic = args.length > 1 ? args[1] : "user-events";
        String outputTopic = args.length > 2 ? args[2] : "session-summaries";
        String groupId = args.length > 3 ? args[3] : "user-session-tracker";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        execute(env, bootstrapServers, inputTopic, outputTopic, groupId);
    }

    public static void execute(StreamExecutionEnvironment env,
                               String bootstrapServers,
                               String inputTopic,
                               String outputTopic,
                               String groupId) throws Exception {

        env.enableCheckpointing(30_000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(10_000);
        env.getCheckpointConfig().setCheckpointTimeout(60_000);

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
                "User Events Source"
        );

        DataStream<UserEvent> events = rawStream.map(UserSessionJob::parseEvent);

        DataStream<String> summaries = trackSessions(events);

        summaries.sinkTo(sink);
        summaries.print("SESSION");

        env.execute("User Session Tracking Job");
    }

    public static DataStream<String> trackSessions(DataStream<UserEvent> events) {
        return events
                .keyBy(UserEvent::getUserId)
                .process(new SessionTracker());
    }

    static class SessionTracker extends KeyedProcessFunction<String, UserEvent, String> {

        private transient ValueState<Long> eventCount;
        private transient ValueState<Long> sessionStart;
        private transient MapState<String, Long> actionCounts;

        @Override
        public void open(OpenContext openContext) {
            eventCount = getRuntimeContext().getState(
                    new ValueStateDescriptor<>("event-count", Types.LONG));

            sessionStart = getRuntimeContext().getState(
                    new ValueStateDescriptor<>("session-start", Types.LONG));

            actionCounts = getRuntimeContext().getMapState(
                    new MapStateDescriptor<>("action-counts", Types.STRING, Types.LONG));
        }

        @Override
        public void processElement(UserEvent event,
                                   KeyedProcessFunction<String, UserEvent, String>.Context ctx,
                                   Collector<String> out) throws Exception {

            Long currentCount = eventCount.value();
            long newCount = (currentCount == null) ? 1 : currentCount + 1;
            eventCount.update(newCount);

            if (sessionStart.value() == null) {
                sessionStart.update(event.getTimestamp());
            }

            String action = event.getAction();
            Long actionCount = actionCounts.get(action);
            actionCounts.put(action, (actionCount == null) ? 1 : actionCount + 1);

            Map<String, Long> counts = new HashMap<>();
            actionCounts.entries().forEach(entry -> counts.put(entry.getKey(), entry.getValue()));

            SessionSummary summary = SessionSummary.builder()
                    .userId(event.getUserId())
                    .totalEvents(newCount)
                    .actionCounts(counts)
                    .sessionStartTime(sessionStart.value())
                    .lastEventTime(event.getTimestamp())
                    .build();

            out.collect(formatSummary(summary));
        }
    }

    static UserEvent parseEvent(String json) {
        String userId = extractJsonField(json, "userId");
        String action = extractJsonField(json, "action");
        long timestamp = Long.parseLong(extractJsonField(json, "timestamp"));

        return UserEvent.builder()
                .userId(userId)
                .action(action)
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

    private static String formatSummary(SessionSummary summary) {
        StringBuilder actions = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Long> entry : summary.getActionCounts().entrySet()) {
            if (!first) actions.append(",");
            actions.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            first = false;
        }
        actions.append("}");

        return "{\"userId\":\"" + summary.getUserId() + "\""
                + ",\"totalEvents\":" + summary.getTotalEvents()
                + ",\"actionCounts\":" + actions
                + ",\"sessionStartTime\":" + summary.getSessionStartTime()
                + ",\"lastEventTime\":" + summary.getLastEventTime() + "}";
    }
}
