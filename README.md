# HandymanHub API

A production-ready REST API for connecting customers with local blue-collar gig workers — electricians, plumbers, carpenters, masons, painters, and construction contractor teams.

Built as part of the InternSpark backend engineering program.

---

## What it does

HandymanHub solves a real problem in India — finding reliable skilled workers for home repair and construction is still done by word of mouth. This platform enables:

- Customers to search available workers by **trade skill + pincode**
- Booking of **individual workers** or **full contractor teams** for multi-day jobs
- A complete **booking lifecycle** with status tracking
- **Verified contractor** system — only vetted contractors can be booked

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.6 |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Testing | JUnit 5 + Mockito |
| Logging | Logback |
| Containerization | Docker + Docker Compose |
| API Docs | Swagger UI (SpringDoc OpenAPI) |

---

## Architecture

```
Controller → Service → Repository → MySQL
↑              ↑
DTOs        Business Rules
```
Layered architecture with strict separation:
- **Controller** — HTTP handling, input validation, DTO mapping
- **Service** — business logic, transaction management
- **Repository** — Spring Data JPA, custom JPQL queries
- **Model** — JPA entities mapped to MySQL tables

---

## Database Schema
```
skills          — trade catalogue (Electrician, Plumber, Mason...)
contractors     — verified contractor companies
workers         — individual gig workers (optionally under a contractor)
worker_skills   — many-to-many: workers ↔ skills with experience years
customers       — people who book services
bookings        — service bookings linking customer + worker/contractor + skill
```
10 Flyway migrations manage the schema. Never edited — always forward.

---

## Key Features

### Worker search by skill + location
```
GET /api/v1/workers/search?skillId=1&pincode=110024
```
Returns all available workers in that pincode with that skill, experience years, and availability status.

### Booking state machine
```
PENDING → CONFIRMED → IN_PROGRESS → COMPLETED
↓
CANCELLED
```
Invalid transitions return 400 with the exact attempted transition in the error message.

### Business rules enforced
- Worker OR contractor — never both, never neither
- Worker must be available and not already booked on that date
- Only verified contractors can be booked
- Reviews only allowed on COMPLETED bookings

### Contractor team model
A contractor brings their full crew. Book Ramesh Kumar → get his team of masons and tile workers for the entire renovation job.

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/skills` | All trade skills |
| POST | `/api/v1/skills` | Create a skill |
| GET | `/api/v1/workers/search?skillId=&pincode=` | Search workers by skill + area |
| POST | `/api/v1/workers` | Register a worker |
| PATCH | `/api/v1/workers/{id}/availability` | Toggle availability |
| POST | `/api/v1/workers/{id}/skills/{skillId}` | Assign skill to worker |
| GET | `/api/v1/contractors` | All contractors |
| PATCH | `/api/v1/contractors/{id}/verify` | Verify a contractor |
| POST | `/api/v1/customers` | Register a customer |
| POST | `/api/v1/bookings` | Create a booking |
| PATCH | `/api/v1/bookings/{id}/status` | Update booking status |
| PATCH | `/api/v1/bookings/{id}/cancel` | Cancel a booking |

Full interactive documentation available at `/swagger-ui.html` after running the app.

---

## Running with Docker

### Prerequisites
- Docker Desktop installed and running

### One command setup
```bash
git clone https://github.com/yourusername/handymanhub.git
cd handymanhub
docker-compose up
```

That's it. Docker will:
1. Pull MySQL 8 image
2. Build the Spring Boot application
3. Wait for MySQL to be healthy
4. Run all 10 Flyway migrations automatically
5. Seed realistic sample data
6. Start the API on port 8081

### Verify it's running
```bash
curl http://localhost:8081/api/v1/skills
```

### View API documentation
```
http://localhost:8081/swagger-ui.html
```

---

## Running Locally (without Docker)

### Prerequisites
- Java 17+
- MySQL 8+
- Maven 3.9+

### Database setup
```sql
CREATE DATABASE handymanhub_db;
CREATE USER 'hman_user'@'localhost' IDENTIFIED BY 'hman_pass';
GRANT ALL PRIVILEGES ON handymanhub_db.* TO 'hman_user'@'localhost';
FLUSH PRIVILEGES;
```

### Run
```bash
./mvnw spring-boot:run
```

Flyway runs migrations automatically on startup.

---

## Running Tests
```bash
./mvnw test
```
```
Tests run: 13, Failures: 0, Errors: 0
```
Test coverage includes:
- Happy path booking creation (worker + contractor)
- All 5 business rule violations
- State machine transitions
- Cancellation rules

---

## Sample API Calls

### Search for an electrician in Delhi 110024
```bash
curl "http://localhost:8081/api/v1/workers/search?skillId=1&pincode=110024"
```

### Create a booking
```bash
curl -X POST http://localhost:8081/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "workerId": 5,
    "skillId": 1,
    "scheduledDate": "2026-08-01",
    "durationDays": 2,
    "address": "B-12 Lajpat Nagar, New Delhi",
    "notes": "Fix wiring in kitchen"
  }'
```

### Book a contractor team
```bash
curl -X POST http://localhost:8081/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "contractorId": 1,
    "skillId": 5,
    "scheduledDate": "2026-09-01",
    "durationDays": 15,
    "address": "Plot 7 DLF Phase 3, Gurugram",
    "notes": "Full bathroom renovation"
  }'
```

### Confirm then start a booking
```bash
curl -X PATCH "http://localhost:8081/api/v1/bookings/1/status?status=CONFIRMED"
curl -X PATCH "http://localhost:8081/api/v1/bookings/1/status?status=IN_PROGRESS"
```

---

## Project Structure
```
src/main/java/com/handymanhub/
├── controller/        REST endpoints
├── service/           Business logic
├── repository/        Spring Data JPA
├── model/             JPA entities
├── dto/
│   ├── request/       Input DTOs with validation
│   └── response/      Output DTOs (immutable)
└── exception/         Global error handling
src/main/resources/
└── db/migration/      10 Flyway SQL files (V1-V10)
```
---

## Known Optimizations (Future Scope)

- **N+1 query problem** — `GET /api/v1/bookings` fires multiple queries per booking. Fix: `JOIN FETCH` in JPQL or `@EntityGraph`
- **Pagination** — all list endpoints return full results. Fix: Spring Data `Pageable`
- **Worker notifications** — availability toggle is manual. Fix: WebSocket push when booking is confirmed
- **JWT Authentication** — all endpoints are currently open. Fix: Spring Security + JWT

---

## Author

Shresth Bhargava| VIT Bhopal | Computer Science Engineering 2028

[LinkedIn](https://linkedin.com/in/shresth-bhargava) · [GitHub](https://github.com/shresthbhargava)