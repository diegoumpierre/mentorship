package com.poc.flink.job;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WordCountJobTest {

    @Test
    void tokenizerSplitsWordsAndLowercases() {
        WordCountJob.Tokenizer tokenizer = new WordCountJob.Tokenizer();
        List<Tuple2<String, Integer>> results = new ArrayList<>();

        Collector<Tuple2<String, Integer>> collector = new Collector<>() {
            @Override
            public void collect(Tuple2<String, Integer> record) {
                results.add(record);
            }

            @Override
            public void close() {}
        };

        tokenizer.flatMap("Hello World HELLO", collector);

        assertThat(results).containsExactly(
                new Tuple2<>("hello", 1),
                new Tuple2<>("world", 1),
                new Tuple2<>("hello", 1)
        );
    }

    @Test
    void tokenizerIgnoresEmptyStrings() {
        WordCountJob.Tokenizer tokenizer = new WordCountJob.Tokenizer();
        List<Tuple2<String, Integer>> results = new ArrayList<>();

        Collector<Tuple2<String, Integer>> collector = new Collector<>() {
            @Override
            public void collect(Tuple2<String, Integer> record) {
                results.add(record);
            }

            @Override
            public void close() {}
        };

        tokenizer.flatMap("", collector);

        assertThat(results).isEmpty();
    }

    @Test
    void tokenizerHandlesSpecialCharacters() {
        WordCountJob.Tokenizer tokenizer = new WordCountJob.Tokenizer();
        List<Tuple2<String, Integer>> results = new ArrayList<>();

        Collector<Tuple2<String, Integer>> collector = new Collector<>() {
            @Override
            public void collect(Tuple2<String, Integer> record) {
                results.add(record);
            }

            @Override
            public void close() {}
        };

        tokenizer.flatMap("java25-flink: hello!", collector);

        assertThat(results).containsExactly(
                new Tuple2<>("java25", 1),
                new Tuple2<>("flink", 1),
                new Tuple2<>("hello", 1)
        );
    }

    @Test
    void executeRunsWithoutErrors() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        WordCountJob.execute(env, "hello world hello");
    }

    @Test
    void executeHandlesMultipleLines() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        WordCountJob.execute(env, "hello world\nflink streaming");
    }
}
