package com.poc.flink.service;

import com.poc.flink.config.FlinkConfig;
import com.poc.flink.config.KafkaConfig;
import com.poc.flink.job.FraudDetectionJob;
import com.poc.flink.job.KafkaWordCountJob;
import com.poc.flink.job.WindowedWordCountJob;
import com.poc.flink.job.WordCountJob;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class FlinkJobService {

    private static final Logger log = Logger.getLogger(FlinkJobService.class.getName());
    private static final String ENTRY_CLASS = "com.poc.flink.job.WordCountJob";

    private final FlinkConfig flinkConfig;
    private final KafkaConfig kafkaConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public FlinkJobService(FlinkConfig flinkConfig, KafkaConfig kafkaConfig) {
        this.flinkConfig = flinkConfig;
        this.kafkaConfig = kafkaConfig;
    }

    public String submitWordCountJob(String text) {
        try {
            String jarId = uploadJar();
            log.info("JAR uploaded, id: " + jarId);
            return runJar(jarId, text);
        } catch (Exception e) {
            log.severe("Job failed " + e.getMessage());
            return "Job failed " + e.getMessage();
        }
    }

    public String runWordCountLocally(String text) {
        try {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(1);
            log.info("Running job locally");
            WordCountJob.execute(env, text);
            return "Job completed";
        } catch (Exception e) {
            log.severe("Job failed " + e.getMessage());
            return "Job failed: " + e.getMessage();
        }
    }

    public String submitWindowedWordCountJob(String text, int windowSeconds) {
        try {
            String jarId = uploadJar();
            log.info("JAR uploaded, id: " + jarId);
            return runJar(jarId, text);
        } catch (Exception e) {
            log.severe("Job failed " + e.getMessage());
            return "Job failed " + e.getMessage();
        }
    }

    public String runWindowedWordCountLocally(String text, int windowSeconds) {
        try {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(1);
            log.info("Running windowed word count locally with " + windowSeconds + "s window");
            WindowedWordCountJob.execute(env, text, windowSeconds);
            return "Job completed";
        } catch (Exception e) {
            log.severe("Job failed " + e.getMessage());
            return "Job failed: " + e.getMessage();
        }
    }

    public String runKafkaWordCountLocally(int windowSeconds) {
        try {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(1);
            log.info("Running Kafka word count locally - reading from " + kafkaConfig.getInputTopic()
                    + ", writing to " + kafkaConfig.getOutputTopic());
            KafkaWordCountJob.execute(env,
                    kafkaConfig.getBootstrapServers(),
                    kafkaConfig.getInputTopic(),
                    kafkaConfig.getOutputTopic(),
                    kafkaConfig.getGroupId(),
                    windowSeconds);
            return "Kafka Word Count job started";
        } catch (Exception e) {
            log.severe("Kafka job failed: " + e.getMessage());
            return "Kafka job failed: " + e.getMessage();
        }
    }

    public String runFraudDetectionLocally() {
        try {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(1);
            log.info("Running Fraud Detection CEP locally - reading from " + kafkaConfig.getTransactionsTopic()
                    + ", alerts to " + kafkaConfig.getFraudAlertsTopic());
            FraudDetectionJob.execute(env,
                    kafkaConfig.getBootstrapServers(),
                    kafkaConfig.getTransactionsTopic(),
                    kafkaConfig.getFraudAlertsTopic(),
                    "fraud-detection");
            return "Fraud Detection CEP job started";
        } catch (Exception e) {
            log.severe("Fraud Detection job failed: " + e.getMessage());
            return "Fraud Detection job failed: " + e.getMessage();
        }
    }

    public String submitFraudDetectionJob() {
        try {
            String jarId = uploadJar();
            log.info("JAR uploaded, id: " + jarId);
            return runFraudDetectionJar(jarId);
        } catch (Exception e) {
            log.severe("Fraud Detection job submission failed: " + e.getMessage());
            return "Fraud Detection job submission failed: " + e.getMessage();
        }
    }

    public String submitKafkaWordCountJob(int windowSeconds) {
        try {
            String jarId = uploadJar();
            log.info("JAR uploaded, id: " + jarId);
            return runKafkaJar(jarId, windowSeconds);
        } catch (Exception e) {
            log.severe("Kafka job submission failed: " + e.getMessage());
            return "Kafka job submission failed: " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getClusterStatus() {
        try {
            return restTemplate.getForObject(flinkConfig.getRestUrl() + "/overview", Map.class);
        } catch (Exception e) {
            log.severe("Failed to reach Flink REST API: " + e.getMessage());
            return Map.of("error", "Cannot connect to Flink: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> listJobs() {
        try {
            return restTemplate.getForObject(flinkConfig.getRestUrl() + "/jobs/overview", Map.class);
        } catch (Exception e) {
            log.severe("Failed to list Flink jobs: " + e.getMessage());
            return Map.of("error", "Cannot list jobs: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private String uploadJar() {
        File jarFile = new File(flinkConfig.getJarPath());
        if (!jarFile.exists()) {
            throw new IllegalStateException("JAR not found at: " + jarFile.getAbsolutePath() + " — run 'mvn package -DskipTests' first");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("jarfile", new FileSystemResource(jarFile));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        Map<String, Object> response = restTemplate.postForObject(flinkConfig.getRestUrl() + "/jars/upload", request, Map.class);

        String filename = (String) response.get("filename");
        return filename.substring(filename.lastIndexOf("/") + 1);
    }

    @SuppressWarnings("unchecked")
    private String runJar(String jarId, String text) {
        Map<String, Object> body = Map.of(
                "entryClass", ENTRY_CLASS,
                "programArgsList", List.of(text)
        );

        Map<String, Object> response = restTemplate.postForObject(
                flinkConfig.getRestUrl() + "/jars/" + jarId + "/run", body, Map.class);

        return "Job submitted successfully, jobId: " + response.get("jobid");
    }

    @SuppressWarnings("unchecked")
    private String runKafkaJar(String jarId, int windowSeconds) {
        Map<String, Object> body = Map.of(
                "entryClass", "com.poc.flink.job.KafkaWordCountJob",
                "programArgsList", List.of(
                        kafkaConfig.getBootstrapServers(),
                        kafkaConfig.getInputTopic(),
                        kafkaConfig.getOutputTopic(),
                        kafkaConfig.getGroupId(),
                        String.valueOf(windowSeconds)
                )
        );

        Map<String, Object> response = restTemplate.postForObject(
                flinkConfig.getRestUrl() + "/jars/" + jarId + "/run", body, Map.class);

        return "Kafka job submitted successfully, jobId: " + response.get("jobid");
    }

    @SuppressWarnings("unchecked")
    private String runFraudDetectionJar(String jarId) {
        Map<String, Object> body = Map.of(
                "entryClass", "com.poc.flink.job.FraudDetectionJob",
                "programArgsList", List.of(
                        kafkaConfig.getBootstrapServers(),
                        kafkaConfig.getTransactionsTopic(),
                        kafkaConfig.getFraudAlertsTopic(),
                        "fraud-detection"
                )
        );

        Map<String, Object> response = restTemplate.postForObject(
                flinkConfig.getRestUrl() + "/jars/" + jarId + "/run", body, Map.class);

        return "Fraud Detection job submitted successfully, jobId: " + response.get("jobid");
    }
}
