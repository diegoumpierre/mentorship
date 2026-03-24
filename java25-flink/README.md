# Java 25 + Apache Flink

Spring Boot application integrated with Apache Flink for distributed stream processing. Demonstrates how to submit and manage Flink jobs through a REST API.

## Stack

| Technology | Version |
|---|---|
| Spring Boot | 3.4.4 |
| Apache Flink | 2.0.0 |
| Springdoc OpenAPI | 2.8.6 |
| Lombok | 1.18.40 |

## Setup

### 1. Start the Flink cluster
```bash
docker compose up -d
```

This starts the **JobManager** (port 8081) and the **TaskManager** (4 task slots).

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

The application starts on port **8080**.

## API Endpoints

Base URL: `/api/flink`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/status` | Flink cluster status |
| `GET` | `/jobs` | List running jobs |
| `POST` | `/jobs/wordcount` | Submit word count job to remote cluster |
| `POST` | `/jobs/wordcount/local` | Run word count locally |
| `POST` | `/jobs/windowed-wordcount` | Submit windowed word count to remote cluster |
| `POST` | `/jobs/windowed-wordcount/local` | Run windowed word count locally |

### Usage example

```bash
# Cluster status
curl http://localhost:8080/api/flink/status

# Submit word count
curl -X POST http://localhost:8080/api/flink/jobs/wordcount \
  -H "Content-Type: application/json" \
  -d '{"text": "hello world hello flink"}'

# Submit windowed word count (10s tumbling window)
curl -X POST http://localhost:8080/api/flink/jobs/windowed-wordcount/local \
  -H "Content-Type: application/json" \
  -d '{"text": "hello world hello flink world flink flink", "windowSeconds": "10"}'
```

## API Documentation

Swagger UI available at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Architecture

```
Spring Boot App (8080)
  |
  |-- FlinkController        -> REST endpoints
  |-- FlinkJobService        -> JAR upload + job submission
  |-- FlinkConfig            -> Connection configuration
  |-- WordCountJob           -> Flink job (tokenizes text and counts words)
  |
  v
Flink Cluster (Docker)
  |-- JobManager (8081)
  |-- TaskManager (4 slots)
```

## Monitoring

- **Flink Dashboard:** [http://localhost:8081](http://localhost:8081)
- **Actuator Health:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Actuator Metrics:** [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)

## Roadmap — Next Features to Implement

### 1. Event Time + Watermarks + Windowing

Replace the simple WordCount with a streaming job that uses **time windows** (tumbling/sliding). For example: count words every 10 seconds. This covers fundamental Flink concepts: event time, watermarks, and window functions.

### 2. Kafka as Source/Sink

Add Kafka to docker-compose and have Flink consume from one topic and produce to another. This makes the pipeline realistic — almost every real-world Flink use case involves Kafka.

### 3. CEP (Complex Event Processing)

Use Flink's CEP library to detect **patterns in event sequences**. For example: fraud detection — "if a user makes 3 purchases in different cities within 5 minutes, trigger an alert". This is one of Flink's most powerful and distinctive features.

### 4. State Management + Checkpointing

Implement a job that uses **keyed state** (ValueState/MapState) with checkpointing enabled. For example: a user session counter that survives failures. This demonstrates fault tolerance, which is Flink's key differentiator.

### 5. Side Outputs

Use side outputs to split events into multiple streams. For example: process a stream and separate valid events from invalid ones (dead letter queue pattern).

### 6. Flink SQL / Table API

Create SQL queries over streams. Flink 2.0 has great improvements in this area. For example: `SELECT word, COUNT(*) FROM words GROUP BY TUMBLE(ts, INTERVAL '10' SECOND), word`.
