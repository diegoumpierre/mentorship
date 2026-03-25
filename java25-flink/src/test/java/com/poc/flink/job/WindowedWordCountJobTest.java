package com.poc.flink.job;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class WindowedWordCountJobTest {

    @Test
    void executeRunsWithTumblingWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        assertThatCode(() -> WindowedWordCountJob.execute(env, "hello world hello flink", 10))
                .doesNotThrowAnyException();
    }

    @Test
    void executeHandlesSmallWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        assertThatCode(() -> WindowedWordCountJob.execute(env, "hello world hello flink world flink flink", 1))
                .doesNotThrowAnyException();
    }

    @Test
    void executeHandlesSingleWord() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        assertThatCode(() -> WindowedWordCountJob.execute(env, "flink", 5))
                .doesNotThrowAnyException();
    }

    @Test
    void executeHandlesMultipleLines() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        assertThatCode(() -> WindowedWordCountJob.execute(env, "hello world\nflink streaming\nhello flink", 10))
                .doesNotThrowAnyException();
    }
}
