# Java 25 + Apache Flink

Spring Boot application integrated with Apache Flink for distributed stream processing. Demonstrates how to submit and manage Flink jobs through a REST API, covering 7 distinct streaming patterns from basic word counting to complex event processing.

## Stack

| Technology | Version |
|---|---|
| Java | 25 |
| Spring Boot | 3.4.4 |
| Apache Flink | 2.0.0 |
| Apache Kafka | 7.6.0 (Confluent) |
| Springdoc OpenAPI | 2.8.6 |
| Lombok | 1.18.40 |

## Jobs Overview

| Job | Flink Pattern | Input | Output | Description |
|---|---|---|---|---|
| WordCountJob | Batch (collection source) | In-memory text | Console | Tokenizes text and counts word occurrences |
| WindowedWordCountJob | Event-time tumbling windows | In-memory collection | Console | Counts words in configurable time windows with watermarks |
| KafkaWordCountJob | Processing-time windows + Kafka I/O | `words-input` | `words-output` | Continuous word counting from a Kafka stream |
| FraudDetectionJob | CEP (Complex Event Processing) | `transactions-input` | `fraud-alerts` | Detects 3 purchases in different cities within 5 minutes |
| UserSessionJob | Keyed state + checkpointing | `user-events` | `session-summaries` | Tracks per-user session stats with fault tolerance |
| EventValidationJob | Side outputs (dead letter) | `raw-events` | `valid-events` / `dead-letter-queue` | Validates events and routes invalid ones to a dead-letter queue |
| SqlWordCountJob | Flink SQL / Table API | `words-input` | Console (changelog) | SQL-based tumbling window word count |

## Architecture

```
                         Spring Boot App (8080)
                               |
          +--------------------+--------------------+
          |                    |                     |
   FlinkController      FlinkJobService         FlinkConfig
   (REST endpoints)    (job orchestration)    (connection props)
          |                    |
          |       +------------+------------+
          |       |  local     |  remote    |
          |       v            v            v
          |   MiniCluster   Flink Cluster (Docker)
          |                  |-- JobManager (8081)
          |                  |-- TaskManager (4 slots)
          |
          +---> Kafka (9092)
                 |-- words-input
                 |-- words-output
                 |-- transactions-input
                 |-- fraud-alerts
                 |-- user-events
                 |-- session-summaries
                 |-- raw-events
                 |-- valid-events
                 |-- dead-letter-queue
```

## Kafka Topics

| Topic | Partitions | Producer | Consumer | Format |
|---|---|---|---|---|
| `words-input` | 3 | Manual / external | KafkaWordCountJob, SqlWordCountJob | Plain text |
| `words-output` | 3 | KafkaWordCountJob | Manual / external | `word:count` |
| `transactions-input` | 3 | Manual / external | FraudDetectionJob | JSON |
| `fraud-alerts` | 3 | FraudDetectionJob | Manual / external | JSON |
| `user-events` | auto | Manual / external | UserSessionJob | JSON |
| `session-summaries` | auto | UserSessionJob | Manual / external | JSON |
| `raw-events` | auto | Manual / external | EventValidationJob | JSON |
| `valid-events` | auto | EventValidationJob | Manual / external | JSON |
| `dead-letter-queue` | auto | EventValidationJob | Manual / external | JSON |

## Setup

### 1. Start infrastructure

```bash
docker compose up -d
```

This starts **Zookeeper**, **Kafka** (port 9092), **Flink JobManager** (port 8081), and **Flink TaskManager** (4 task slots). Kafka topics `words-input`, `words-output`, `transactions-input`, and `fraud-alerts` are created automatically.

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

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `GET` | `/status` | Flink cluster status | — |
| `GET` | `/jobs` | List running jobs | — |
| `POST` | `/jobs/wordcount` | Submit word count (remote) | `{"text": "..."}` |
| `POST` | `/jobs/wordcount/local` | Run word count (local) | `{"text": "..."}` |
| `POST` | `/jobs/windowed-wordcount` | Submit windowed word count (remote) | `{"text": "...", "windowSeconds": "10"}` |
| `POST` | `/jobs/windowed-wordcount/local` | Run windowed word count (local) | `{"text": "...", "windowSeconds": "10"}` |
| `POST` | `/jobs/kafka-wordcount` | Submit Kafka word count (remote) | `{"windowSeconds": "10"}` |
| `POST` | `/jobs/kafka-wordcount/local` | Run Kafka word count (local) | `{"windowSeconds": "10"}` |
| `POST` | `/jobs/fraud-detection` | Submit fraud detection (remote) | — |
| `POST` | `/jobs/fraud-detection/local` | Run fraud detection (local) | — |
| `POST` | `/jobs/user-session` | Submit user session tracking (remote) | — |
| `POST` | `/jobs/user-session/local` | Run user session tracking (local) | — |
| `POST` | `/jobs/event-validation` | Submit event validation (remote) | — |
| `POST` | `/jobs/event-validation/local` | Run event validation (local) | — |
| `POST` | `/jobs/sql-wordcount` | Submit SQL word count (remote) | `{"windowSeconds": "10"}` |
| `POST` | `/jobs/sql-wordcount/local` | Run SQL word count (local) | `{"windowSeconds": "10"}` |

## API Documentation

Swagger UI available at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Demo Scenarios

Below are end-to-end walkthroughs. Each scenario produces test data, triggers a job, and shows how to observe results.

### Scenario 1: Word Count (local, no Kafka)

The simplest job — runs entirely in-memory.

```bash
# Submit and see results in the application console
curl -X POST http://localhost:8080/api/flink/jobs/wordcount/local \
  -H "Content-Type: application/json" \
  -d '{"text": "hello world hello flink world flink flink"}'
```

Expected output in application logs:
```
(flink,3)
(hello,2)
(world,2)
```

### Scenario 2: Streaming Word Count with Kafka

Continuous word counting from a Kafka topic with tumbling windows.

**Step 1 — Start the job:**
```bash
curl -X POST http://localhost:8080/api/flink/jobs/kafka-wordcount/local \
  -H "Content-Type: application/json" \
  -d '{"windowSeconds": "10"}'
```

**Step 2 — Produce messages (in a separate terminal):**
```bash
docker compose exec kafka kafka-console-producer \
  --broker-list kafka:29092 \
  --topic words-input <<EOF
hello world hello flink
stream processing is great
hello flink flink flink
EOF
```

**Step 3 — Consume results:**
```bash
docker compose exec kafka kafka-console-consumer \
  --bootstrap-server kafka:29092 \
  --topic words-output \
  --from-beginning
```

Expected output (after window closes):
```
flink:4
hello:3
world:1
stream:1
processing:1
is:1
great:1
```

### Scenario 3: Fraud Detection (CEP)

Detects when a user makes 3 purchases in 3 different cities within 5 minutes.

**Step 1 — Start the job:**
```bash
curl -X POST http://localhost:8080/api/flink/jobs/fraud-detection/local
```

**Step 2 — Produce transactions that trigger a fraud alert:**

```bash
# Three purchases from the same user in different cities within 5 minutes
NOW=$(date +%s)000

docker compose exec kafka kafka-console-producer \
  --broker-list kafka:29092 \
  --topic transactions-input <<EOF
{"userId":"user42","city":"New York","amount":150.00,"timestamp":${NOW}}
{"userId":"user42","city":"London","amount":200.00,"timestamp":$((${NOW} + 60000))}
{"userId":"user42","city":"Tokyo","amount":300.00,"timestamp":$((${NOW} + 120000))}
EOF
```

**Step 3 — Consume fraud alerts:**
```bash
docker compose exec kafka kafka-console-consumer \
  --bootstrap-server kafka:29092 \
  --topic fraud-alerts \
  --from-beginning
```

Expected output:
```json
{"userId":"user42","cities":["New York","London","Tokyo"],"amounts":[150.0,200.0,300.0],"reason":"3 purchases in 3 different cities within 5 minutes"}
```

**Negative test — same city (no alert):**
```bash
docker compose exec kafka kafka-console-producer \
  --broker-list kafka:29092 \
  --topic transactions-input <<EOF
{"userId":"user99","city":"Paris","amount":50.00,"timestamp":${NOW}}
{"userId":"user99","city":"Paris","amount":75.00,"timestamp":$((${NOW} + 60000))}
{"userId":"user99","city":"Paris","amount":100.00,"timestamp":$((${NOW} + 120000))}
EOF
```

No alert will be generated since all purchases are in the same city.

### Scenario 4: Event Validation + Dead Letter Queue

Validates incoming events and separates valid from invalid ones.

**Step 1 — Start the job:**
```bash
curl -X POST http://localhost:8080/api/flink/jobs/event-validation/local
```

**Step 2 — Produce a mix of valid and invalid events:**
```bash
docker compose exec kafka kafka-console-producer \
  --broker-list kafka:29092 \
  --topic raw-events <<EOF
{"userId":"user1","action":"login","timestamp":1700000000000}
{"userId":"user2","action":"click","timestamp":1700000001000}
not-json-at-all
{"userId":"user3","action":"INVALID_ACTION","timestamp":1700000002000}
{"action":"purchase","timestamp":1700000003000}
{"userId":"user4","action":"purchase","timestamp":1700000004000}
EOF
```

**Step 3 — Consume valid events:**
```bash
docker compose exec kafka kafka-console-consumer \
  --bootstrap-server kafka:29092 \
  --topic valid-events \
  --from-beginning
```

Expected: `user1/login`, `user2/click`, `user4/purchase` (3 valid events)

**Step 4 — Consume dead-letter queue:**
```bash
docker compose exec kafka kafka-console-consumer \
  --bootstrap-server kafka:29092 \
  --topic dead-letter-queue \
  --from-beginning
```

Expected: `not-json-at-all` (bad JSON), `user3/INVALID_ACTION` (unknown action), missing userId event (3 invalid events)

### Scenario 5: User Session Tracking

Tracks per-user session statistics with exactly-once state management.

**Step 1 — Start the job:**
```bash
curl -X POST http://localhost:8080/api/flink/jobs/user-session/local
```

**Step 2 — Produce user events:**
```bash
docker compose exec kafka kafka-console-producer \
  --broker-list kafka:29092 \
  --topic user-events <<EOF
{"userId":"alice","action":"login","timestamp":1700000000000}
{"userId":"alice","action":"click","timestamp":1700000010000}
{"userId":"bob","action":"login","timestamp":1700000020000}
{"userId":"alice","action":"purchase","timestamp":1700000030000}
{"userId":"bob","action":"click","timestamp":1700000040000}
{"userId":"alice","action":"logout","timestamp":1700000050000}
EOF
```

**Step 3 — Consume session summaries:**
```bash
docker compose exec kafka kafka-console-consumer \
  --bootstrap-server kafka:29092 \
  --topic session-summaries \
  --from-beginning
```

Expected: running session summaries for `alice` (4 events: login, click, purchase, logout) and `bob` (2 events: login, click), each with action counts.

## Data Flow Diagrams

### Word Count Jobs

```
words-input (Kafka)
      |
      v
  [Tokenizer] --> keyBy(word) --> [TumblingWindow] --> [Sum]
                                                         |
                                                         v
                                                  words-output (Kafka)
```

### Fraud Detection (CEP)

```
transactions-input (Kafka)
      |
      v
  keyBy(userId) --> [CEP Pattern: 3 cities in 5min]
                          |
                     match found?
                    /           \
                  yes            no
                  |              |
                  v            (skip)
           fraud-alerts (Kafka)
```

### User Session Tracking

```
user-events (Kafka)
      |
      v
  keyBy(userId) --> [KeyedProcessFunction]
                      |-- ValueState<eventCount>
                      |-- MapState<action, count>
                      |
                      v
               session-summaries (Kafka)
```

### Event Validation (Side Outputs)

```
raw-events (Kafka)
      |
      v
  [ProcessFunction: validate JSON, fields, action]
      |                    |
   valid               invalid
      |                    |
      v                    v
valid-events       dead-letter-queue
   (Kafka)              (Kafka)
```

### SQL Word Count

```
words-input (Kafka)
      |
      v
  [Table API: fromDataStream]
      |
      v
  SQL: SELECT word, COUNT(*)
       FROM words
       GROUP BY TUMBLE(rowtime, INTERVAL '10' SECOND), word
      |
      v
  Changelog stream (console)
```

## Monitoring

- **Flink Dashboard:** [http://localhost:8081](http://localhost:8081)
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Actuator Health:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Actuator Metrics:** [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)

## Roadmap

All originally planned features have been implemented:

- [x] Event Time + Watermarks + Windowing (WindowedWordCountJob)
- [x] Kafka as Source/Sink (KafkaWordCountJob)
- [x] CEP — Complex Event Processing (FraudDetectionJob)
- [x] State Management + Checkpointing (UserSessionJob)
- [x] Side Outputs (EventValidationJob)
- [x] Flink SQL / Table API (SqlWordCountJob)
