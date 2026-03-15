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

## Keycloak (JWT claim mappers)

Resource server config uses `KEYCLOAK_ISSUER_URI` and expects a tenant claim and roles in the JWT.

### Realm + client setup
1. Create a realm, e.g. `urbangate`.
2. Create a client for the backend, e.g. `urbangate-backend`. Set client authentication ON (confidential) and standard flow OFF (resource server).
3. Configure the backend with `KEYCLOAK_ISSUER_URI=https://<host>/realms/urbangate`.
4. Set `urbangate.security.enabled=true`.
5. Set `urbangate.security.roles-claim=realm_access.roles`.
6. Set `urbangate.security.resource-client-id=urbangate-backend` only if using client roles.

### Tenant claim mapper (`estate_id`)
1. Add a user attribute named `estate_id` (UUID string).
2. Add a mapper:
3. Mapper type: User Attribute.
4. User attribute: `estate_id`.
5. Token claim name: `estate_id`.
6. Claim JSON type: `String`.
7. Add to access token: ON.
8. Backend expects `urbangate.security.tenant-claim=estate_id`.

### Roles mapping
- Realm roles appear in `realm_access.roles` automatically.
- Client roles appear in `resource_access.<clientId>.roles` if assigned.

### Admin client for password reset
1. Create a confidential client (e.g. `urbangate-admin`).
2. Enable service accounts.
3. Assign realm-management role `manage-users`.
4. Configure `urbangate.keycloak.admin.enabled=true`.
5. Configure `KEYCLOAK_BASE_URL=https://<host>`.
6. Configure `KEYCLOAK_REALM=urbangate`.
7. Configure `KEYCLOAK_CLIENT_ID=urbangate-admin`.
8. Configure `KEYCLOAK_CLIENT_SECRET=<secret>`.

### Tenant header fallback (for unauthenticated onboarding)
Set `urbangate.security.allow-header-tenant=true` and send `X-Estate-Id` if the request is not JWT-authenticated.

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
