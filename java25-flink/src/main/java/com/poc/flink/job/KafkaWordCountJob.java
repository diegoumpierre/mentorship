package com.poc.flink.job;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class KafkaWordCountJob {

    public static void main(String[] args) throws Exception {
        String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        String inputTopic = args.length > 1 ? args[1] : "words-input";
        String outputTopic = args.length > 2 ? args[2] : "words-output";
        String groupId = args.length > 3 ? args[3] : "flink-word-count";
        int windowSeconds = args.length > 4 ? Integer.parseInt(args[4]) : 10;

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        execute(env, bootstrapServers, inputTopic, outputTopic, groupId, windowSeconds);
    }

    public static void execute(StreamExecutionEnvironment env,
                               String bootstrapServers,
                               String inputTopic,
                               String outputTopic,
                               String groupId,
                               int windowSeconds) throws Exception {

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

        DataStream<String> inputStream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
        );

        DataStream<String> wordCounts = inputStream
                .flatMap((String line, Collector<Tuple2<String, Integer>> out) -> {
                    for (String word : line.toLowerCase().split("\\W+")) {
                        if (!word.isEmpty()) {
                            out.collect(new Tuple2<>(word, 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(windowSeconds)))
                .sum(1)
                .map(tuple -> tuple.f0 + ":" + tuple.f1)
                .returns(Types.STRING);

        wordCounts.sinkTo(sink);
        wordCounts.print();

        env.execute("Kafka Word Count Job");
    }
}