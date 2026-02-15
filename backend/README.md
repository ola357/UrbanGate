# UrbanGate Backend (Bootstrap)

**Tech**
- Java 21 (Gradle toolchains)
- Spring Boot 4 (Spring MVC)
- PostgreSQL + Flyway
- Redis (optional caching / rate limiting later)
- Quality: Spotless, Checkstyle, SpotBugs, JaCoCo, SonarCloud

## Quick start (local)

Prereqs: Docker + Java 21

```bash
cd backend
docker compose up -d postgres redis
./gradlew clean check
./gradlew :modules:app:bootRun

Or run the app in Docker:
```bash
docker compose up --build app
```
```

API:
- `GET http://localhost:8080/api/v1/version`
- `GET http://localhost:8080/actuator/health`

## Local infrastructure
`docker compose` provisions:
- Postgres (`5432`)
- Redis (`6379`)
- SonarQube (`9000`) for local inspection (optional)


## Docker Compose Override

By default, the `docker-compose.override.yml` disables the `app` container
so that only infrastructure (Postgres, Redis, SonarQube) starts.

Start infra only:
```bash
docker compose up -d
```

Start backend container explicitly:
```bash
docker compose up --build app
```
