package com.poc.flink.controller;

import com.poc.flink.service.FlinkJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FlinkControllerTest {

    private FlinkController controller;
    private StubFlinkJobService stubService;

    @BeforeEach
    void setUp() {
        stubService = new StubFlinkJobService();
        controller = new FlinkController(stubService);
    }

    @Test
    void getClusterStatusReturnsOk() {
        stubService.clusterStatus = Map.of("taskmanagers", 1);

        ResponseEntity<Map<String, Object>> response = controller.getClusterStatus();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("taskmanagers", 1);
    }

    @Test
    void listJobsReturnsOk() {
        stubService.jobsList = Map.of("jobs", "[]");

        ResponseEntity<Map<String, Object>> response = controller.listJobs();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("jobs", "[]");
    }

    @Test
    void submitWordCountReturnsResult() {
        stubService.wordCountResult = "Job submitted successfully, jobId: abc123";

        ResponseEntity<String> response = controller.submitWordCount(Map.of("text", "hello world"));

        assertThat(response.getBody()).isEqualTo("Job submitted successfully, jobId: abc123");
    }

    @Test
    void runWordCountLocalReturnsResult() {
        stubService.wordCountLocalResult = "Job completed";

        ResponseEntity<String> response = controller.runWordCountLocal(Map.of("text", "hello flink"));

        assertThat(response.getBody()).isEqualTo("Job completed");
    }

    @Test
    void submitWindowedWordCountReturnsResult() {
        stubService.windowedResult = "Job submitted successfully, jobId: def456";

        ResponseEntity<String> response = controller.submitWindowedWordCount(
                Map.of("text", "hello world", "windowSeconds", "5"));

        assertThat(response.getBody()).isEqualTo("Job submitted successfully, jobId: def456");
    }

    @Test
    void runWindowedWordCountLocalReturnsResult() {
        stubService.windowedLocalResult = "Job completed";

        ResponseEntity<String> response = controller.runWindowedWordCountLocal(
                Map.of("text", "hello flink", "windowSeconds", "10"));

        assertThat(response.getBody()).isEqualTo("Job completed");
    }

    @Test
    void submitKafkaWordCountReturnsResult() {
        stubService.kafkaResult = "Kafka job submitted successfully, jobId: ghi789";

        ResponseEntity<String> response = controller.submitKafkaWordCount(Map.of("windowSeconds", "10"));

        assertThat(response.getBody()).isEqualTo("Kafka job submitted successfully, jobId: ghi789");
    }

    @Test
    void runKafkaWordCountLocalReturnsResult() {
        stubService.kafkaLocalResult = "Kafka Word Count job started";

        ResponseEntity<String> response = controller.runKafkaWordCountLocal(Map.of("windowSeconds", "10"));

        assertThat(response.getBody()).isEqualTo("Kafka Word Count job started");
    }

    @Test
    void submitWordCountUsesDefaultText() {
        stubService.wordCountResult = "Job submitted";

        ResponseEntity<String> response = controller.submitWordCount(Map.of());

        assertThat(response.getBody()).isEqualTo("Job submitted");
        assertThat(stubService.lastWordCountText).isEqualTo("submit word Count");
    }

    @Test
    void submitSqlWordCountReturnsResult() {
        stubService.sqlWordCountResult = "SQL Word Count job submitted successfully, jobId: sql123";

        ResponseEntity<String> response = controller.submitSqlWordCount(Map.of("windowSeconds", "10"));

        assertThat(response.getBody()).isEqualTo("SQL Word Count job submitted successfully, jobId: sql123");
    }

    @Test
    void runSqlWordCountLocalReturnsResult() {
        stubService.sqlWordCountLocalResult = "SQL Word Count job started";

        ResponseEntity<String> response = controller.runSqlWordCountLocal(Map.of("windowSeconds", "10"));

        assertThat(response.getBody()).isEqualTo("SQL Word Count job started");
    }

    @Test
    void submitEventValidationReturnsResult() {
        stubService.eventValidationResult = "Event Validation job submitted successfully, jobId: ev123";

        ResponseEntity<String> response = controller.submitEventValidation();

        assertThat(response.getBody()).isEqualTo("Event Validation job submitted successfully, jobId: ev123");
    }

    @Test
    void runEventValidationLocalReturnsResult() {
        stubService.eventValidationLocalResult = "Event Validation job started";

        ResponseEntity<String> response = controller.runEventValidationLocal();

        assertThat(response.getBody()).isEqualTo("Event Validation job started");
    }

    @Test
    void submitFraudDetectionReturnsResult() {
        stubService.fraudDetectionResult = "Fraud Detection job submitted successfully, jobId: fraud123";

        ResponseEntity<String> response = controller.submitFraudDetection();

        assertThat(response.getBody()).isEqualTo("Fraud Detection job submitted successfully, jobId: fraud123");
    }

    @Test
    void runFraudDetectionLocalReturnsResult() {
        stubService.fraudDetectionLocalResult = "Fraud Detection CEP job started";

        ResponseEntity<String> response = controller.runFraudDetectionLocal();

        assertThat(response.getBody()).isEqualTo("Fraud Detection CEP job started");
    }

    @Test
    void submitUserSessionReturnsResult() {
        stubService.userSessionResult = "User Session job submitted successfully, jobId: session123";

        ResponseEntity<String> response = controller.submitUserSession();

        assertThat(response.getBody()).isEqualTo("User Session job submitted successfully, jobId: session123");
    }

    @Test
    void runUserSessionLocalReturnsResult() {
        stubService.userSessionLocalResult = "User Session Tracking job started";

        ResponseEntity<String> response = controller.runUserSessionLocal();

        assertThat(response.getBody()).isEqualTo("User Session Tracking job started");
    }

    static class StubFlinkJobService extends FlinkJobService {

        String wordCountResult = "";
        String wordCountLocalResult = "";
        String windowedResult = "";
        String windowedLocalResult = "";
        String kafkaResult = "";
        String kafkaLocalResult = "";
        String sqlWordCountResult = "";
        String sqlWordCountLocalResult = "";
        String eventValidationResult = "";
        String eventValidationLocalResult = "";
        String fraudDetectionResult = "";
        String fraudDetectionLocalResult = "";
        String userSessionResult = "";
        String userSessionLocalResult = "";
        Map<String, Object> clusterStatus = Map.of();
        Map<String, Object> jobsList = Map.of();
        String lastWordCountText;

        StubFlinkJobService() {
            super(null, null);
        }

        @Override
        public String submitWordCountJob(String text) {
            lastWordCountText = text;
            return wordCountResult;
        }

        @Override
        public String runWordCountLocally(String text) {
            return wordCountLocalResult;
        }

        @Override
        public String submitWindowedWordCountJob(String text, int windowSeconds) {
            return windowedResult;
        }

        @Override
        public String runWindowedWordCountLocally(String text, int windowSeconds) {
            return windowedLocalResult;
        }

        @Override
        public String submitKafkaWordCountJob(int windowSeconds) {
            return kafkaResult;
        }

        @Override
        public String runKafkaWordCountLocally(int windowSeconds) {
            return kafkaLocalResult;
        }

        @Override
        public String submitSqlWordCountJob(int windowSeconds) {
            return sqlWordCountResult;
        }

        @Override
        public String runSqlWordCountLocally(int windowSeconds) {
            return sqlWordCountLocalResult;
        }

        @Override
        public String submitEventValidationJob() {
            return eventValidationResult;
        }

        @Override
        public String runEventValidationLocally() {
            return eventValidationLocalResult;
        }

        @Override
        public String submitFraudDetectionJob() {
            return fraudDetectionResult;
        }

        @Override
        public String runFraudDetectionLocally() {
            return fraudDetectionLocalResult;
        }

        @Override
        public String submitUserSessionJob() {
            return userSessionResult;
        }

        @Override
        public String runUserSessionLocally() {
            return userSessionLocalResult;
        }

        @Override
        public Map<String, Object> getClusterStatus() {
            return clusterStatus;
        }

        @Override
        public Map<String, Object> listJobs() {
            return jobsList;
        }
    }
}
