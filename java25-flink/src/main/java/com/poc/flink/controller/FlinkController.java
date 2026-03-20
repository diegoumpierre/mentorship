package com.poc.flink.controller;

import com.poc.flink.service.FlinkJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/flink")
public class FlinkController {

    private final FlinkJobService flinkJobService;

    public FlinkController(FlinkJobService flinkJobService) {
        this.flinkJobService = flinkJobService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getClusterStatus() {
        return ResponseEntity.ok(flinkJobService.getClusterStatus());
    }

    @GetMapping("/jobs")
    public ResponseEntity<Map<String, Object>> listJobs() {
        return ResponseEntity.ok(flinkJobService.listJobs());
    }

    @PostMapping("/jobs/wordcount")
    public ResponseEntity<String> submitWordCount(@RequestBody Map<String, String> request) {
        String text = request.getOrDefault("text", "submit word Count");
        String result = flinkJobService.submitWordCountJob(text);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/jobs/wordcount/local")
    public ResponseEntity<String> runWordCountLocal(@RequestBody Map<String, String> request) {
        String text = request.getOrDefault("text", "submit word Count 2");
        String result = flinkJobService.runWordCountLocally(text);
        return ResponseEntity.ok(result);
    }
}
