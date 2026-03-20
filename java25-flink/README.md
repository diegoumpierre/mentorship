# Java 25 + Apache Flink

Aplicacao Spring Boot integrada com Apache Flink para processamento distribuido de streams. Demonstra como submeter e gerenciar jobs Flink atraves de uma API REST.

## Stack

| Tecnologia | Versao |
|---|---|
| Spring Boot | 3.4.4 |
| Apache Flink | 2.0.0 |
| Springdoc OpenAPI | 2.8.6 |
| Lombok | 1.18.40 |

## Setup

### 1. Subir o cluster Flink
```bash
docker compose up -d
```

Isso inicia o **JobManager** (porta 8081) e o **TaskManager** (4 task slots).
### 2. Compilar o projeto

```bash
mvn clean install
```

### 3. Rodar a aplicacao

```bash
mvn spring-boot:run
```

A aplicacao sobe na porta **8080**.

## API Endpoints

Base URL: `/api/flink`

| Metodo | Endpoint | Descricao |
|---|---|---|
| `GET` | `/status` | Status do cluster Flink |
| `GET` | `/jobs` | Lista jobs em execucao |
| `POST` | `/jobs/wordcount` | Submete job de word count no cluster remoto |
| `POST` | `/jobs/wordcount/local` | Executa word count localmente |

### Exemplo de uso

```bash
# Status do cluster
curl http://localhost:8080/api/flink/status

# Submeter word count
curl -X POST http://localhost:8080/api/flink/jobs/wordcount \
  -H "Content-Type: application/json" \
  -d '{"text": "hello world hello flink"}'
```

## Documentacao da API

Swagger UI disponivel em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Arquitetura

```
Spring Boot App (8080)
  |
  |-- FlinkController        -> Endpoints REST
  |-- FlinkJobService        -> Upload de JAR + submissao de jobs
  |-- FlinkConfig            -> Configuracao de conexao
  |-- WordCountJob           -> Job Flink (tokeniza texto e conta palavras)
  |
  v
Flink Cluster (Docker)
  |-- JobManager (8081)
  |-- TaskManager (4 slots)
```

## Monitoramento

- **Flink Dashboard:** [http://localhost:8081](http://localhost:8081)
- **Actuator Health:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Actuator Metrics:** [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)
