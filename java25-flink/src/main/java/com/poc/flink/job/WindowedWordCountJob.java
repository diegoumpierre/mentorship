package com.poc.flink.job;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WindowedWordCountJob {

    public static void main(String[] args) throws Exception {
        String text = args.length > 0 ? String.join(" ", args) : "Test";
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        execute(env, text, 10);
    }

    public static void execute(StreamExecutionEnvironment env, String inputText, int windowSeconds) throws Exception {
        List<Tuple3<String, Integer, Long>> wordEvents = buildEvents(inputText);

        DataStream<Tuple3<String, Integer, Long>> source = env.fromCollection(wordEvents)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Tuple3<String, Integer, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                                .withTimestampAssigner((event, timestamp) -> event.f2)
                );

        source.keyBy(tuple -> tuple.f0)
                .window(TumblingEventTimeWindows.of(Duration.ofSeconds(windowSeconds)))
                .sum(1)
                .print();

        env.execute("Windowed Word Count Job");
    }

    private static List<Tuple3<String, Integer, Long>> buildEvents(String inputText) {
        List<Tuple3<String, Integer, Long>> events = new ArrayList<>();
        long baseTime = System.currentTimeMillis();
        int index = 0;

        for (String line : inputText.split("\\n")) {
            for (String word : line.toLowerCase().split("\\W+")) {
                if (!word.isEmpty()) {
                    events.add(new Tuple3<>(word, 1, baseTime + (index * 1000L)));
                    index++;
                }
            }
        }

        return events;
    }
}
