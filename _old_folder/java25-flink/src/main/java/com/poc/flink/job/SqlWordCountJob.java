package com.poc.flink.job;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

public class SqlWordCountJob {

    public static void main(String[] args) throws Exception {
        String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        String inputTopic = args.length > 1 ? args[1] : "words-input";
        String groupId = args.length > 2 ? args[2] : "sql-word-count";
        int windowSeconds = args.length > 3 ? Integer.parseInt(args[3]) : 10;

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        executeKafka(env, bootstrapServers, inputTopic, groupId, windowSeconds);
    }

    public static void executeKafka(StreamExecutionEnvironment env,
                                    String bootstrapServers,
                                    String inputTopic,
                                    String groupId,
                                    int windowSeconds) throws Exception {

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(inputTopic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> rawStream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
        );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Table resultTable = runWordCountSql(tableEnv, rawStream, windowSeconds);

        tableEnv.toChangelogStream(resultTable)
                .print("SQL WORD COUNT");

        env.execute("SQL Word Count Job");
    }

    public static Table runWordCountSql(StreamTableEnvironment tableEnv,
                                        DataStream<String> lines,
                                        int windowSeconds) {

        DataStream<WordEvent> words = lines
                .flatMap((String line, org.apache.flink.util.Collector<WordEvent> out) -> {
                    long now = System.currentTimeMillis();
                    for (String word : line.toLowerCase().split("\\W+")) {
                        if (!word.isEmpty()) {
                            out.collect(new WordEvent(word, now));
                        }
                    }
                })
                .returns(WordEvent.class)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<WordEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner((event, ts) -> event.ts)
                );

        return buildAndQuery(tableEnv, words, windowSeconds);
    }

    public static Table buildAndQuery(StreamTableEnvironment tableEnv,
                                      DataStream<WordEvent> words,
                                      int windowSeconds) {

        Table wordsTable = tableEnv.fromDataStream(
                words,
                Schema.newBuilder()
                        .column("word", "STRING")
                        .column("ts", "BIGINT")
                        .columnByMetadata("rowtime", DataTypes.TIMESTAMP_LTZ(3), "rowtime")
                        .watermark("rowtime", "SOURCE_WATERMARK()")
                        .build()
        );

        tableEnv.createTemporaryView("words", wordsTable);

        return tableEnv.sqlQuery(
                "SELECT word, COUNT(*) AS cnt, " +
                "window_start, window_end " +
                "FROM TABLE(TUMBLE(TABLE words, DESCRIPTOR(rowtime), INTERVAL '" + windowSeconds + "' SECOND)) " +
                "GROUP BY word, window_start, window_end"
        );
    }

    public static class WordEvent {
        public String word;
        public long ts;

        public WordEvent() {}

        public WordEvent(String word, long ts) {
            this.word = word;
            this.ts = ts;
        }
    }
}
