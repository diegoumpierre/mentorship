package com.poc.flink.job;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;

public class WordCountJob {

    public static void main(String[] args) throws Exception {

        String text = args.length > 0 ? String.join(" ", args) : "Test";
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        execute(env, text);
    }

    public static void execute(StreamExecutionEnvironment env, String inputText) throws Exception {
        List<String> lines = Arrays.asList(inputText.split("\\n"));
        DataStream<String> textStream = env.fromCollection(lines);
        DataStream<Tuple2<String, Integer>> wordCounts = textStream
                .flatMap(new Tokenizer())
                .keyBy(tuple -> tuple.f0)
                .sum(1);

        wordCounts.print();
        env.execute("Word Count Job");
    }

    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            for (String word : value.toLowerCase().split("\\W+")) {
                if (!word.isEmpty()) {
                    out.collect(new Tuple2<>(word, 1));
                }
            }
        }
    }

}
