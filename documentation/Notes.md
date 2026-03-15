This is a deep-dive evaluation across backend, mobile, infra, and CI/CD with PRD alignment and cost focus.
Executive Summary
• The workspace is a strong scaffold (multi-module backend, CI/CD, infra), but feature implementation is near-zero relative to the PRD. Current backend exposes only /api/v1/version plus health endpoints; mobile is a demo home + health screen.
• The infra/CI setup is already capable of deploying a containerized backend to Azure Container Apps with PostgreSQL, but there are environment/config mismatches and missing security wiring.
• The biggest risks are product delivery gap (PRD vs code), security/auth not implemented, and environment configuration that can cause production builds to point to dev services.
PRD Coverage vs Current Implementation
• Onboarding/Sign-In/Registration: Not implemented (no data model, endpoints, or mobile flows).
• Homescreen: Only a placeholder home screen exists; no PRD widgets or navigation.
• Visitor Access & Events: Not implemented.
• Billing: Not implemented.
• Wallet: Not implemented.
• Directory: Not implemented.
• Profile: Not implemented.
  PRD reference: PRD.xlsx

Key Findings (Actionable Issues)
1. High: Mobile defaults to HTTPS for localhost, which will fail without TLS. This conflicts with the README and .env.example that use HTTP.
◦ env.ts
◦ .env.example
2. High: Production mobile build is pointed at a dev backend URL. Staging is a placeholder. This risks shipping a prod build hitting dev.
◦ eas.json
3. Medium: spring.profiles.active defaults to dev in the base application.yml. If env vars are not set, production runs will boot in dev mode.
◦ application.yml
4. Medium: Security is implied in infra (Keycloak issuer URI, URBANGATE_SECURITY_ENABLED) but no security configuration or resource server wiring exists in code. This becomes critical once real endpoints land.
◦ container_app.tf
◦ Backend code shows no security config beyond dependencies: build.gradle.kts
Architecture & Codebase Observations
• The backend is a modular monolith with context modules scaffolded (iam, access, billing, wallet, etc.) but only the app module is populated.
◦ settings.gradle.kts
• Current backend code is a minimal Spring Boot application with a version endpoint and a standardized API error handler.
◦ UrbanGateApplication.java
◦ VersionController.java
◦ ApiExceptionHandler.java
• There’s a baseline Outbox table in migration V1, which is a good forward-looking architectural decision but not yet used anywhere.
◦ V1__baseline.sql
Mobile App Observations
• The app is currently a skeleton: fetches /api/v1/version and exposes a health screen.
◦ HomeScreen.tsx

• The structure is clean (feature folders + React Query), so it will scale, but no PRD features are implemented.
Infra & DevOps Observations (Cost + Risk)
• Azure Container Apps with min_replicas = 0 is cost-aware for backend compute.
    ◦ container_app.tf
• PostgreSQL Flexible Server default SKU is B_Standard_B1ms with 32GB storage. This is low but still a fixed cost. If cost is a priority, consider smaller storage or a cheaper dev-only database for non-production environments.
    ◦ variables.tf
• Log Analytics is enabled with 30-day retention. This can be reduced to cut cost in dev.
    ◦ log_analytics.tf
• CI/CD is well-structured and includes Sonar, Spotless, Checkstyle, and image deployment to GHCR + Azure ACA.
    ◦ .github/workflows/backend-ci.yml
    ◦ .github/workflows/mobile-ci.yml
    ◦ .github/workflows/infra.yml
Quality & Testing
• Good baseline quality gates are present, with tests for the minimal API and exception handling.
• There are no integration tests, contract tests, or load tests yet. That’s expected for the current stage, but will be required once core features land.
    ◦ backend tests
Strategic Gaps vs PRD (Design-Level)
• Multi-tenancy is central to the PRD, but there’s no tenant model, tenant context, or schema strategy yet.
• The PRD assumes an Estate Admin workflow (CSV upload, pre-registration, activation codes). That implies an Admin portal or internal API and a data model for estates/residents that does not exist.
• Security requirements (OTP, account lockout, password reset) require a real IAM subsystem plus rate limiting and audit trails.


Cost Optimization Notes
• Keep the modular monolith; avoid microservices until traffic or team size justifies it.
• Use a single dev environment early; gate staging/prod builds until core flows stabilize.
• Reduce log retention in dev to 7–14 days.
• Defer non-critical infra components (e.g., Redis) until actually used in flows.
Open Questions
1. Is Keycloak the intended identity provider, or do you want a custom IAM service?
2. Do you want a lightweight Admin Portal for estate pre-registration now, or should it be an internal backend-only flow?
3. What is the MVP slice from the PRD for the next 4–6 weeks?
Suggested Next Steps (Prioritized)
1. Confirm auth strategy and multi-tenant model (schema + tenant resolution).
2. Implement core onboarding domain: Estate, Resident, ActivationCode, LoginAttempt, PasswordReset.
3. Build minimal Admin API for pre-registration + code dispatch.
4. Implement the mobile onboarding + login flow.
5. Add Visitor Access “Individual Code” as the first PRD feature beyond auth.
6. Add basic audit logging + rate limiting (Redis is already provisioned).
