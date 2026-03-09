

# BloomLog – In-Memory High-Performance Log Service

**BloomLog** is a **Spring Boot** web application for fast, in-memory log ingestion and querying. It uses **SHA-256 hashing** and **Bloom filters** to provide **efficient exact-match searches**, while also supporting **substring queries** and **time-range filtering**. Logs are stored entirely in memory, and old logs are automatically cleaned up daily. No database is required.

---

## **Features**

* **Fast In-Memory Log Storage** – Uses `ConcurrentHashMap` for thread-safe log storage.
* **Bloom Filter Optimization** – Pre-hashes logs for quick existence checks.
* **Exact Match & Substring Queries** – Choose between fast Bloom filter queries or full scan substring searches.
* **Time-Range Filtering** – Query logs within specific timestamps.
* **Automatic Cleanup** – Removes logs older than a retention period and trims memory usage daily.
* **REST API** – Simple endpoints for ingestion, querying, and inspection.

---

## **REST API Endpoints**

| Endpoint             | Method | Description                                                      |
| -------------------- | ------ | ---------------------------------------------------------------- |
| `/logs/ingest`       | POST   | Ingest a log (`?message=...`)                                    |
| `/logs/query`        | GET    | Query logs (`?message=...&exact=true/false&startTime=&endTime=`) |
| `/logs/mightContain` | GET    | Check if a log might exist (Bloom filter)                        |
| `/logs/all`          | GET    | Retrieve all logs                                                |

**Query Parameters:**

* `exact=true/false` → Exact match vs substring search.
* `startTime` & `endTime` → Optional time-range filter in milliseconds.

---

## **Example Usage**

* **Ingest a log**:

```
POST /logs/ingest?message=Server started
```

* **Exact-match query**:

```
GET /logs/query?message=Server started
```

* **Substring query**:

```
GET /logs/query?message=Server&exact=false
```

* **Time-range query**:

```
GET /logs/query?message=Error&exact=false&startTime=1678377600000&endTime=1678464000000
```

* **Check if log might exist**:

```
GET /logs/mightContain?message=Server started
```

---

## **Getting Started**

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/BloomLog.git
cd BloomLog
```

2. **Build the project**

```bash
mvn clean install
```

3. **Run the application**

```bash
mvn spring-boot:run
```

4. **Access REST API**

```
http://localhost:8080/logs/
```

---

## **Advantages**

* Lightweight, **no external database** needed.
* High performance with **Bloom filter exact-match queries**.
* Flexible **substring search** and **time-range filtering**.
* **Automatic memory management** through daily cleanup.

---


