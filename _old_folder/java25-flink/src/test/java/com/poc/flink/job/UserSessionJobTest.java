package com.poc.flink.job;

import com.poc.flink.model.UserEvent;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.junit5.MiniClusterExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserSessionJobTest {

    @RegisterExtension
    static final MiniClusterExtension MINI_CLUSTER = new MiniClusterExtension(
            new MiniClusterResourceConfiguration.Builder()
                    .setNumberTaskManagers(1)
                    .setNumberSlotsPerTaskManager(2)
                    .build()
    );

    @Test
    void parseEventFromJson() {
        String json = "{\"userId\":\"user1\",\"action\":\"click\",\"timestamp\":1700000000000}";

        UserEvent event = UserSessionJob.parseEvent(json);

        assertThat(event.getUserId()).isEqualTo("user1");
        assertThat(event.getAction()).isEqualTo("click");
        assertThat(event.getTimestamp()).isEqualTo(1700000000000L);
    }

    @Test
    void parseEventFailsOnMissingField() {
        String json = "{\"userId\":\"user1\",\"action\":\"click\"}";

        assertThatThrownBy(() -> UserSessionJob.parseEvent(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("timestamp");
    }

    @Test
    void trackSessionsCountsEventsPerUser() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = 1700000000000L;

        DataStream<UserEvent> events = env.fromData(
                UserEvent.builder().userId("user1").action("login").timestamp(now).build(),
                UserEvent.builder().userId("user1").action("click").timestamp(now + 1000).build(),
                UserEvent.builder().userId("user1").action("click").timestamp(now + 2000).build(),
                UserEvent.builder().userId("user1").action("purchase").timestamp(now + 3000).build()
        );

        DataStream<String> summaries = UserSessionJob.trackSessions(events);

        var result = summaries.executeAndCollect(10);

        // 4 events = 4 summaries emitted (one per event, each with updated state)
        assertThat(result).hasSize(4);

        // Last summary should have the accumulated totals
        String lastSummary = result.get(3);
        assertThat(lastSummary).contains("\"totalEvents\":4");
        assertThat(lastSummary).contains("\"userId\":\"user1\"");
        assertThat(lastSummary).contains("\"click\":2");
        assertThat(lastSummary).contains("\"login\":1");
        assertThat(lastSummary).contains("\"purchase\":1");
    }

    @Test
    void trackSessionsIsolatesStateByUser() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = 1700000000000L;

        DataStream<UserEvent> events = env.fromData(
                UserEvent.builder().userId("user1").action("click").timestamp(now).build(),
                UserEvent.builder().userId("user2").action("click").timestamp(now + 1000).build(),
                UserEvent.builder().userId("user1").action("click").timestamp(now + 2000).build(),
                UserEvent.builder().userId("user2").action("purchase").timestamp(now + 3000).build()
        );

        DataStream<String> summaries = UserSessionJob.trackSessions(events);

        var result = summaries.executeAndCollect(10);

        assertThat(result).hasSize(4);

        // Each user should have their own independent count
        // Find the last summary for each user
        String lastUser1 = result.stream()
                .filter(s -> s.contains("\"userId\":\"user1\""))
                .reduce((first, second) -> second)
                .orElseThrow();
        String lastUser2 = result.stream()
                .filter(s -> s.contains("\"userId\":\"user2\""))
                .reduce((first, second) -> second)
                .orElseThrow();

        assertThat(lastUser1).contains("\"totalEvents\":2");
        assertThat(lastUser2).contains("\"totalEvents\":2");
        assertThat(lastUser2).contains("\"purchase\":1");
    }

    @Test
    void trackSessionsTracksSessionStartTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long startTime = 1700000000000L;

        DataStream<UserEvent> events = env.fromData(
                UserEvent.builder().userId("user1").action("login").timestamp(startTime).build(),
                UserEvent.builder().userId("user1").action("click").timestamp(startTime + 60_000).build()
        );

        DataStream<String> summaries = UserSessionJob.trackSessions(events);

        var result = summaries.executeAndCollect(10);

        // Both summaries should have the same session start time (the first event)
        for (String summary : result) {
            assertThat(summary).contains("\"sessionStartTime\":" + startTime);
        }

        // Last event time should be updated
        String lastSummary = result.get(1);
        assertThat(lastSummary).contains("\"lastEventTime\":" + (startTime + 60_000));
    }

    @Test
    void trackSessionsSingleEvent() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = 1700000000000L;

        DataStream<UserEvent> events = env.fromData(
                UserEvent.builder().userId("user1").action("login").timestamp(now).build()
        );

        DataStream<String> summaries = UserSessionJob.trackSessions(events);

        var result = summaries.executeAndCollect(10);

        assertThat(result).hasSize(1);
        String summary = result.get(0);
        assertThat(summary).contains("\"totalEvents\":1");
        assertThat(summary).contains("\"login\":1");
        assertThat(summary).contains("\"sessionStartTime\":" + now);
        assertThat(summary).contains("\"lastEventTime\":" + now);
    }
}
