# LogSight 🔍

> A lightweight, high-performance in-memory log ingestion and query service — built with Spring Boot, SHA-256 hashing, and Bloom filters for blazing-fast log lookups with zero database overhead.

---

## ✨ Features

| Feature | Description |
|---|---|
| ⚡ **Fast In-Memory Storage** | `ConcurrentHashMap` provides thread-safe, low-latency log storage |
| 🌸 **Bloom Filter Optimization** | SHA-256 pre-hashing enables O(1) existence checks before any scan |
| 🔎 **Flexible Querying** | Supports exact-match (Bloom filter), substring search, and time-range filtering |
| 🕒 **Time-Range Filtering** | Retrieve logs within precise millisecond-precision timestamp windows |
| 🧹 **Automatic Cleanup** | Scheduled daily retention policy trims stale logs and reclaims memory |
| 🌐 **RESTful API** | Simple, intuitive endpoints for ingestion, querying, and inspection |
| 🪶 **Zero Dependencies** | No external database, broker, or infrastructure required |

---

## 🛠 Tech Stack

- **Framework:** Spring Boot 3.x
- **Hashing:** SHA-256 (via `java.security.MessageDigest`)
- **Probabilistic Filter:** Bloom Filter
- **Concurrency:** `ConcurrentHashMap`
- **Scheduling:** Spring `@Scheduled` (daily cleanup)
- **Build Tool:** Maven

---

## ⚙️ How It Works

```
Incoming Log
     │
     ▼
[ SHA-256 Hash ]  ──────────────────── Hash the message
     │
     ▼
[ Bloom Filter ]  ──────────────────── Store hash for fast existence checks
     │
     ▼
[ ConcurrentHashMap ] ──────────────── Persist full log entry + timestamp in memory
     │
     ▼
[ Query Engine ]
     ├── exact=true  →  Bloom filter lookup  →  O(1) check
     └── exact=false →  Full scan + substring match + optional time-range filter
```

1. **Ingest** — The log message is hashed with SHA-256 and registered in the Bloom filter, then stored with a timestamp in a concurrent map.
2. **Exact Query** — The Bloom filter provides a near-instant probabilistic check before touching the full log store.
3. **Substring Query** — A full scan is performed across all stored logs, optionally filtered by a time window.
4. **Cleanup** — A daily scheduled task removes logs beyond the configured retention period to keep memory bounded.

---

## 🌐 API Reference

### Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/logs/ingest` | Ingest a new log message |
| `GET` | `/logs/query` | Query logs by message content and/or time range |
| `GET` | `/logs/mightContain` | Bloom filter existence check for a message |
| `GET` | `/logs/all` | Retrieve all stored logs |

### Query Parameters

| Parameter | Type | Description |
|---|---|---|
| `message` | `string` | The log message to search for |
| `exact` | `boolean` | `true` for Bloom filter exact match, `false` for substring scan |
| `startTime` | `long` | Start of time window (Unix milliseconds) |
| `endTime` | `long` | End of time window (Unix milliseconds) |

---

## 📖 Example Usage

**Ingest a log**
```http
POST /logs/ingest?message=Server started
```

**Exact-match query**
```http
GET /logs/query?message=Server started&exact=true
```

**Substring query**
```http
GET /logs/query?message=Server&exact=false
```

**Time-range query**
```http
GET /logs/query?message=Error&exact=false&startTime=1678377600000&endTime=1678464000000
```

**Bloom filter check**
```http
GET /logs/mightContain?message=Server started
```

> ⚠️ **Note on Bloom Filters:** A `mightContain=true` response means the log *probably* exists. A `false` response guarantees it does not. False positives are possible by design.

---

## 🚦 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/sanuh1999/LogSight.git
cd logsight

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run

# 4. API is available at
# http://localhost:8080/logs/
```

---

## 🛣 Roadmap

- [ ] Configurable retention period via `application.properties`
- [ ] Structured log ingestion (JSON payloads with severity levels)
- [ ] Log severity filtering (`INFO`, `WARN`, `ERROR`)
- [ ] Optional persistence layer with **Redis** or **PostgreSQL**
- [ ] Metrics endpoint (`/actuator/prometheus`) for observability
- [ ] Frontend dashboard for real-time log exploration

---

## 🤝 Contributing

Contributions are welcome! Please open an issue to discuss your idea before submitting a pull request.

---


*Built as an exploration of probabilistic data structures and high-performance in-memory systems.*
