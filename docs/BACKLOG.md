# Project Task Backlog — Premium Calculator

This backlog outlines implemented features, active practical tasks, and planned enhancements for future iterations of **Premium Calculator**.

---

## Release 1.0.0 (Current MVP — College Practical Deliverable)

### Completed Epics & Stories

- [x] **EPIC-1: Core Premium Calculation Engine**
  - [x] Implement policy types (Life 1.20%, Health 2.40%, Vehicle 3.10%, Travel 1.60%).
  - [x] Implement mathematical formula engine using `BigDecimal` (Scale 2, RoundingMode.HALF_UP).
  - [x] Add boundary validation (Coverage: ₹1,000–₹10,000,000; Term: 1–50 years).
  - [x] Provide demonstration disclaimers on all calculation outputs.

- [x] **EPIC-2: Transaction Lifecycle & REST API**
  - [x] `POST /api/calculations` — calculate and record transaction in SUBMITTED state.
  - [x] `GET /api/transactions` — list all transactions, latest first.
  - [x] `GET /api/transactions/{id}` — retrieve single transaction by ID.
  - [x] `PATCH /api/transactions/{id}/status` — approve or reject transaction (with state machine guards).
  - [x] `GET /api/transactions/{id}/receipt` — plain-text voucher download.
  - [x] `GET /actuator/health` — Spring Boot Actuator monitoring endpoint.

- [x] **EPIC-3: Modern Responsive Web Interface**
  - [x] Interactive calculator form with quick-amount chips and synced range slider.
  - [x] Live calculation summary cards displaying annual and total premiums.
  - [x] Transaction history table with search query and status filtering.
  - [x] Transaction details popup modal with direct approve/reject/download actions.
  - [x] Actuator heartbeat health dot indicator in the navbar.

- [x] **EPIC-4: Quality Assurance & Automated Testing**
  - [x] Unit tests for `PremiumCalculationService` (14 test cases including parameterized tests).
  - [x] Unit tests for `TransactionService` (9 test cases covering status transitions & receipt generation).
  - [x] Integration tests for `PremiumCalculatorController` with MockMvc (10 test cases).
  - [x] Selenium E2E test suite under dedicated Maven profile (`selenium-tests`).

- [x] **EPIC-5: DevOps & Containerization Pipeline**
  - [x] Production-ready multi-stage `Dockerfile` with Eclipse Temurin 17 JRE and non-root user.
  - [x] `docker-compose.yml` for unified local orchestration.
  - [x] Jenkins CI/CD declarative pipeline (`Jenkinsfile`) with 8 stages and Windows/Linux compatibility.
  - [x] Ansible configuration playbook (`ansible/playbook.yml`) with automated `/actuator/health` verification.

---

## Release 1.1.0 (Planned Enhancements)

- [ ] **PERSIST-1: Relational Database Migration**
  - Integrate Spring Data JPA with PostgreSQL / H2 database.
  - Replace `InMemoryTransactionRepository` with database entity repository.
  - Add Flyway / Liquibase database migrations.

- [ ] **SEC-1: Role-Based Access Control (RBAC)**
  - Add Spring Security with JWT tokens.
  - Differentiate between standard users (calculate & view) and claims managers (approve/reject).

---

## Release 2.0.0 (Advanced Cloud Infrastructure)

- [ ] **K8S-1: Kubernetes Orchestration**
  - Helm chart for deployment, service, ingress, and configmaps.
  - Horizontal Pod Autoscaler (HPA) based on CPU and memory thresholds.

- [ ] **MON-1: Prometheus & Grafana Observability**
  - Expose Micrometer metrics at `/actuator/prometheus`.
  - Build pre-configured Grafana dashboards for transaction counts, latency, and status rates.
