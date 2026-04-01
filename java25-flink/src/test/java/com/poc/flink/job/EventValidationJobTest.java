package com.poc.flink.job;

import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.junit5.MiniClusterExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class EventValidationJobTest {

    @RegisterExtension
    static final MiniClusterExtension MINI_CLUSTER = new MiniClusterExtension(
            new MiniClusterResourceConfiguration.Builder()
                    .setNumberTaskManagers(1)
                    .setNumberSlotsPerTaskManager(2)
                    .build()
    );

    @Test
    void validEventGoesToMainOutput() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData(
                "{\"userId\":\"user1\",\"action\":\"click\",\"timestamp\":1700000000000}"
        );

        EventValidationJob.SplitResult split = EventValidationJob.splitEvents(input);

        var valid = split.valid().executeAndCollect(10);

        assertThat(valid).hasSize(1);
        assertThat(valid.get(0)).contains("user1");
    }

    @Test
    void invalidEventGoesToSideOutput() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData(
                "not json at all"
        );

        EventValidationJob.SplitResult split = EventValidationJob.splitEvents(input);

        var invalid = split.invalid().executeAndCollect(10);

        assertThat(invalid).hasSize(1);
        assertThat(invalid.get(0)).contains("not valid JSON");
        assertThat(invalid.get(0)).contains("\"original\":");
    }

    @Test
    void mixedEventsAreSplitCorrectly() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData(
                "{\"userId\":\"user1\",\"action\":\"login\",\"timestamp\":1700000000000}",
                "broken",
                "{\"userId\":\"user2\",\"action\":\"purchase\",\"timestamp\":1700000001000}",
                "{\"userId\":\"user3\",\"action\":\"fly\",\"timestamp\":1700000002000}",
                "{\"userId\":\"\",\"action\":\"click\",\"timestamp\":1700000003000}"
        );

        EventValidationJob.SplitResult split = EventValidationJob.splitEvents(input);

        var valid = split.valid().executeAndCollect(10);
        var invalid = split.invalid().executeAndCollect(10);

        assertThat(valid).hasSize(2);
        assertThat(invalid).hasSize(3);
    }

    @Test
    void missingFieldGoesToSideOutput() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData(
                "{\"userId\":\"user1\",\"action\":\"click\"}"
        );

        EventValidationJob.SplitResult split = EventValidationJob.splitEvents(input);

        var invalid = split.invalid().executeAndCollect(10);

        assertThat(invalid).hasSize(1);
        assertThat(invalid.get(0)).contains("missing field");
    }

    @Test
    void unknownActionGoesToSideOutput() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData(
                "{\"userId\":\"user1\",\"action\":\"fly\",\"timestamp\":1700000000000}"
        );

        EventValidationJob.SplitResult split = EventValidationJob.splitEvents(input);

        var invalid = split.invalid().executeAndCollect(10);

        assertThat(invalid).hasSize(1);
        assertThat(invalid.get(0)).contains("unknown action: fly");
    }

    @Test
    void blankUserIdGoesToSideOutput() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData(
                "{\"userId\":\"\",\"action\":\"click\",\"timestamp\":1700000000000}"
        );

        EventValidationJob.SplitResult split = EventValidationJob.splitEvents(input);

        var invalid = split.invalid().executeAndCollect(10);

        assertThat(invalid).hasSize(1);
        assertThat(invalid.get(0)).contains("userId is blank");
    }

    @Test
    void negativeTimestampGoesToSideOutput() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData(
                "{\"userId\":\"user1\",\"action\":\"click\",\"timestamp\":-1}"
        );

        EventValidationJob.SplitResult split = EventValidationJob.splitEvents(input);

        var invalid = split.invalid().executeAndCollect(10);

        assertThat(invalid).hasSize(1);
        assertThat(invalid.get(0)).contains("timestamp must be positive");
    }

    @Test
    void validateReturnsValidForGoodEvent() {
        String json = "{\"userId\":\"user1\",\"action\":\"login\",\"timestamp\":1700000000000}";
        var result = EventValidationJob.validate(json);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateReturnsInvalidForEmptyInput() {
        var result = EventValidationJob.validate("");
        assertThat(result.isValid()).isFalse();
        assertThat(result.reason()).isEqualTo("empty input");
    }

    @Test
    void validateReturnsInvalidForNullInput() {
        var result = EventValidationJob.validate(null);
        assertThat(result.isValid()).isFalse();
        assertThat(result.reason()).isEqualTo("empty input");
    }

    @Test
    void allValidActionsAreAccepted() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData(
                "{\"userId\":\"u1\",\"action\":\"login\",\"timestamp\":1700000000000}",
                "{\"userId\":\"u1\",\"action\":\"click\",\"timestamp\":1700000001000}",
                "{\"userId\":\"u1\",\"action\":\"purchase\",\"timestamp\":1700000002000}",
                "{\"userId\":\"u1\",\"action\":\"logout\",\"timestamp\":1700000003000}"
        );

        EventValidationJob.SplitResult split = EventValidationJob.splitEvents(input);

        var valid = split.valid().executeAndCollect(10);
        var invalid = split.invalid().executeAndCollect(10);

        assertThat(valid).hasSize(4);
        assertThat(invalid).isEmpty();
    }
}
