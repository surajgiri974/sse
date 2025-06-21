# Server-Sent Events (SSE)

This project demonstrates a real-time notification system using **Spring Boot 3.5.3**, **PostgreSQL triggers**, and **Server-Sent Events (SSE)**. It allows clients to receive push notifications over HTTP when a `report` table in Postgres is updated or inserted.

---

## Setup

### 1. PostgreSQL

Create a database named `sse`:

```sql
CREATE DATABASE sse;
```

---

### 2. application.properties

```properties
spring.application.name=sse
spring.datasource.url=jdbc:postgresql://localhost:5432/sse
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=none

spring.sql.init.mode=always
spring.sql.init.continue-on-error=true
spring.sql.init.platform=postgres
spring.sql.init.schema-locations=classpath:data.sql
```

---

## PostgreSQL Schema (data.sql)

```sql
CREATE TABLE IF NOT EXISTS report (
    id SERIAL PRIMARY KEY,
    content TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION notify_report_event()
RETURNS trigger AS $$
BEGIN
  PERFORM pg_notify('report_channel', row_to_json(NEW)::text);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

---

## 🔄 API Endpoints

### 🔄 Subscribe to Events (SSE)

```http
GET /sse/subscribe
Accept: text/event-stream
```

Example:

```bash
curl http://localhost:8080/sse/subscribe
```

### 📈 Insert Report via REST

```http
POST /sse/report
Content-Type: application/json
{
  "content": "Test event"
}
```


---

## 🔊 How It Works

* PostgreSQL trigger calls `pg_notify` on any INSERT or UPDATE.
* Java app listens to `report_channel` using JDBC + `PGConnection`.
* On event, it broadcasts data to all connected `SseEmitters`.

---
