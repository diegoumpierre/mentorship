package com.poc.flink.service;

import com.poc.flink.config.FlinkConfig;
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
    private final RestTemplate restTemplate = new RestTemplate();

    public FlinkJobService(FlinkConfig flinkConfig) {
        this.flinkConfig = flinkConfig;
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


}
