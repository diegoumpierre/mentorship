package com.poc.flink.job;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.junit5.MiniClusterExtension;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaWordCountJobTest {

    @RegisterExtension
    static final MiniClusterExtension MINI_CLUSTER = new MiniClusterExtension(
            new MiniClusterResourceConfiguration.Builder()
                    .setNumberTaskManagers(1)
                    .setNumberSlotsPerTaskManager(2)
                    .build()
    );

    @Test
    void flatMapSplitsAndLowercases() {
        List<Tuple2<String, Integer>> results = new ArrayList<>();
        Collector<Tuple2<String, Integer>> collector = new Collector<>() {
            @Override
            public void collect(Tuple2<String, Integer> record) {
                results.add(record);
            }

            @Override
            public void close() {}
        };

        String line = "Hello World HELLO";
        for (String word : line.toLowerCase().split("\\W+")) {
            if (!word.isEmpty()) {
                collector.collect(new Tuple2<>(word, 1));
            }
        }

        assertThat(results).containsExactly(
                new Tuple2<>("hello", 1),
                new Tuple2<>("world", 1),
                new Tuple2<>("hello", 1)
        );
    }

    @Test
    void flatMapIgnoresEmptyWords() {
        List<Tuple2<String, Integer>> results = new ArrayList<>();
        Collector<Tuple2<String, Integer>> collector = new Collector<>() {
            @Override
            public void collect(Tuple2<String, Integer> record) {
                results.add(record);
            }

            @Override
            public void close() {}
        };

        String line = "";
        for (String word : line.toLowerCase().split("\\W+")) {
            if (!word.isEmpty()) {
                collector.collect(new Tuple2<>(word, 1));
            }
        }

        assertThat(results).isEmpty();
    }

    @Test
    void pipelineProducesCorrectWordCounts() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData("hello world hello", "flink world flink flink");

        DataStream<Tuple2<String, Integer>> wordCounts = input
                .flatMap((String line, Collector<Tuple2<String, Integer>> out) -> {
                    for (String word : line.toLowerCase().split("\\W+")) {
                        if (!word.isEmpty()) {
                            out.collect(new Tuple2<>(word, 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(tuple -> tuple.f0)
                .sum(1);

        var result = wordCounts.executeAndCollect(20);

        // Check that final counts are correct
        // hello:2, world:2, flink:3 (accumulated via sum)
        assertThat(result).isNotEmpty();

        // Find the last occurrence of each word (that's the final count)
        Tuple2<String, Integer> lastHello = result.stream()
                .filter(t -> t.f0.equals("hello"))
                .reduce((a, b) -> b).orElseThrow();
        Tuple2<String, Integer> lastWorld = result.stream()
                .filter(t -> t.f0.equals("world"))
                .reduce((a, b) -> b).orElseThrow();
        Tuple2<String, Integer> lastFlink = result.stream()
                .filter(t -> t.f0.equals("flink"))
                .reduce((a, b) -> b).orElseThrow();

        assertThat(lastHello.f1).isEqualTo(2);
        assertThat(lastWorld.f1).isEqualTo(2);
        assertThat(lastFlink.f1).isEqualTo(3);
    }

    @Test
    void pipelineHandlesSpecialCharacters() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData("java25-flink: hello! hello.");

        DataStream<Tuple2<String, Integer>> wordCounts = input
                .flatMap((String line, Collector<Tuple2<String, Integer>> out) -> {
                    for (String word : line.toLowerCase().split("\\W+")) {
                        if (!word.isEmpty()) {
                            out.collect(new Tuple2<>(word, 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(tuple -> tuple.f0)
                .sum(1);

        var result = wordCounts.executeAndCollect(20);

        Tuple2<String, Integer> lastHello = result.stream()
                .filter(t -> t.f0.equals("hello"))
                .reduce((a, b) -> b).orElseThrow();

        assertThat(lastHello.f1).isEqualTo(2);
        assertThat(result.stream().anyMatch(t -> t.f0.equals("java25"))).isTrue();
        assertThat(result.stream().anyMatch(t -> t.f0.equals("flink"))).isTrue();
    }

    @Test
    void pipelineHandlesSingleWord() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.fromData("flink");

        DataStream<Tuple2<String, Integer>> wordCounts = input
                .flatMap((String line, Collector<Tuple2<String, Integer>> out) -> {
                    for (String word : line.toLowerCase().split("\\W+")) {
                        if (!word.isEmpty()) {
                            out.collect(new Tuple2<>(word, 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(tuple -> tuple.f0)
                .sum(1);

        var result = wordCounts.executeAndCollect(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new Tuple2<>("flink", 1));
    }
}
