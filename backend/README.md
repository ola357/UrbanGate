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
docker compose -f docker-compose.deps.yml up -d
./gradlew clean check
./gradlew :modules:app:bootRun

Or run the app in Docker:
```bash
docker compose -f docker-compose.deps.yml -f docker-compose.app.yml up --build app
```
```

API:
- `GET http://localhost:8080/api/v1/version`
- `GET http://localhost:8080/actuator/health`

## Local infrastructure
Compose stacks:
- Postgres (`5432`)
- Redis (`6379`)
- SonarQube (`9000`) for local inspection (optional)


## Docker Compose Scripts

Infra (Postgres + Redis + PGAdmin + RedisInsight):
```bash
docker compose -f docker-compose.deps.yml up -d
```

SonarQube only:
```bash
docker compose -f docker-compose.sonar.yml up -d
```

App (with infra):
```bash
docker compose -f docker-compose.deps.yml -f docker-compose.app.yml up --build app
```

App + SonarQube:
```bash
docker compose -f docker-compose.deps.yml -f docker-compose.app.yml -f docker-compose.sonar.yml up --build app
```
