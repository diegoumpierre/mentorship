package com.poc.flink.job;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SqlWordCountJobTest {

    private DataStream<SqlWordCountJob.WordEvent> withWatermarks(
            StreamExecutionEnvironment env, SqlWordCountJob.WordEvent... events) {
        return env.fromData(events)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<SqlWordCountJob.WordEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner((e, ts) -> e.ts)
                );
    }

    @Test
    void sqlCountsWordsInTumblingWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = System.currentTimeMillis();

        DataStream<SqlWordCountJob.WordEvent> words = withWatermarks(env,
                new SqlWordCountJob.WordEvent("hello", now),
                new SqlWordCountJob.WordEvent("world", now + 100),
                new SqlWordCountJob.WordEvent("hello", now + 200),
                new SqlWordCountJob.WordEvent("hello", now + 300)
        );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Table result = SqlWordCountJob.buildAndQuery(tableEnv, words, 10);

        List<Row> rows = tableEnv.toChangelogStream(result).executeAndCollect(100);

        assertThat(rows).isNotEmpty();

        long helloCount = rows.stream()
                .filter(r -> "hello".equals(r.getField("word")))
                .mapToLong(r -> (Long) r.getField("cnt"))
                .sum();
        long worldCount = rows.stream()
                .filter(r -> "world".equals(r.getField("word")))
                .mapToLong(r -> (Long) r.getField("cnt"))
                .sum();

        assertThat(helloCount).isEqualTo(3);
        assertThat(worldCount).isEqualTo(1);
    }

    @Test
    void sqlHandlesSingleWord() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = System.currentTimeMillis();

        DataStream<SqlWordCountJob.WordEvent> words = withWatermarks(env,
                new SqlWordCountJob.WordEvent("flink", now)
        );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Table result = SqlWordCountJob.buildAndQuery(tableEnv, words, 10);

        List<Row> rows = tableEnv.toChangelogStream(result).executeAndCollect(100);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getField("word")).isEqualTo("flink");
        assertThat(rows.get(0).getField("cnt")).isEqualTo(1L);
    }

    @Test
    void sqlGroupsByWindowBoundaries() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // Align to a 10s window boundary: 0s and 1s in [0, 10), 15s in [10, 20)
        long base = 0L;

        DataStream<SqlWordCountJob.WordEvent> words = withWatermarks(env,
                new SqlWordCountJob.WordEvent("hello", base),
                new SqlWordCountJob.WordEvent("hello", base + 1000),
                new SqlWordCountJob.WordEvent("hello", base + 15_000)
        );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Table result = SqlWordCountJob.buildAndQuery(tableEnv, words, 10);

        List<Row> rows = tableEnv.toChangelogStream(result).executeAndCollect(100);

        assertThat(rows).hasSize(2);

        List<Long> counts = rows.stream()
                .map(r -> (Long) r.getField("cnt"))
                .sorted()
                .toList();
        assertThat(counts).containsExactly(1L, 2L);
    }

    @Test
    void sqlWithMultipleDistinctWords() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        long now = System.currentTimeMillis();

        DataStream<SqlWordCountJob.WordEvent> words = withWatermarks(env,
                new SqlWordCountJob.WordEvent("java", now),
                new SqlWordCountJob.WordEvent("flink", now + 100),
                new SqlWordCountJob.WordEvent("kafka", now + 200),
                new SqlWordCountJob.WordEvent("java", now + 300),
                new SqlWordCountJob.WordEvent("flink", now + 400),
                new SqlWordCountJob.WordEvent("java", now + 500)
        );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Table result = SqlWordCountJob.buildAndQuery(tableEnv, words, 10);

        List<Row> rows = tableEnv.toChangelogStream(result).executeAndCollect(100);

        assertThat(rows).hasSize(3);

        long javaCount = rows.stream()
                .filter(r -> "java".equals(r.getField("word")))
                .mapToLong(r -> (Long) r.getField("cnt"))
                .sum();
        long flinkCount = rows.stream()
                .filter(r -> "flink".equals(r.getField("word")))
                .mapToLong(r -> (Long) r.getField("cnt"))
                .sum();
        long kafkaCount = rows.stream()
                .filter(r -> "kafka".equals(r.getField("word")))
                .mapToLong(r -> (Long) r.getField("cnt"))
                .sum();

        assertThat(javaCount).isEqualTo(3);
        assertThat(flinkCount).isEqualTo(2);
        assertThat(kafkaCount).isEqualTo(1);
    }
}
