# Practical Tasks 1–15 Mapping & Evidence Guide (Proposed Mapping)

> **Important Note:** This document presents a **Proposed Practical Task Mapping** spanning the full Software Development Life Cycle (SDLC) and DevOps pipeline for **Premium Calculator**. If your college department provides an official laboratory task syllabus or specific task wording, please provide the official task sheet to ensure exact 1:1 syllabus alignment.

---

## Student Identity & Project Details
- **Student Name:** Manas Chand
- **Roll Number:** 23102C0030
- **Project Name:** Premium Calculator
- **Repository Name:** `premium-calculator`
- **Application Stack:** Java 17, Spring Boot 3.2.5, Maven, HTML5, CSS3, JavaScript
- **DevOps Stack:** Git, GitHub, Docker, Jenkins, Ansible, Actuator

---

## Practical Task Breakdown (Tasks 1 to 15)

### Task 1: Requirements Engineering & System Design
- **Objective:** Analyze requirements, define system scope, policy rates, calculation formulas, and architectural design.
- **Associated Files:** `docs/ARCHITECTURE.md`, `docs/BACKLOG.md`
- **Key Deliverable:** Documented three-tier architecture, domain entities (`PolicyType`, `PremiumTransaction`), and transparent formula definition.
- **Evidence to Capture:**
  - System architecture diagram from `ARCHITECTURE.md`.
  - Rate matrix table: Life (1.20%), Health (2.40%), Vehicle (3.10%), Travel (1.60%).

---

### Task 2: Git Version Control Setup & Repository Initialization
- **Objective:** Initialize Git version control, define `.gitignore`, configure Git user identity, and create project structure.
- **Associated Files:** `.gitignore`, `CONTRIBUTING.md`, `README.md`
- **Commands Executed:**
  ```powershell
  git init
  git config user.name "Manas Chand"
  git config user.email "manas.chand@college.edu"
  git add .
  git commit -m "chore(init): initialize Premium Calculator project structure"
  ```
- **Evidence to Capture:** Terminal output showing `git init` and initial commit hash.

---

### Task 3: Git Branching Strategy & Workflow Implementation
- **Objective:** Implement the `develop` branching model with feature branches and pull request conventions.
- **Associated Files:** `CONTRIBUTING.md`, `.github/ISSUE_TEMPLATE/*`
- **Commands Executed:**
  ```powershell
  git branch -M main
  git checkout -b develop
  git checkout -b feature/core-calculator-engine
  ```
- **Evidence to Capture:** Terminal showing branch switching (`git branch -a`) and Conventional Commit messages.

---

### Task 4: Backend Spring Boot Project Scaffolding & Configuration
- **Objective:** Bootstrap Spring Boot 3.2.5 with Java 17, Maven wrapper, and Actuator dependencies.
- **Associated Files:** `pom.xml`, `src/main/resources/application.properties`, `mvnw.cmd`
- **Commands Executed:**
  ```powershell
  .\mvnw.cmd -version
  ```
- **Evidence to Capture:** Maven dependency tree and `application.properties` configuration displaying port 8080 and Actuator settings.

---

### Task 5: Business Logic Implementation (Premium Calculation Engine)
- **Objective:** Implement high-precision calculation logic for all four policy types using `BigDecimal` with validation.
- **Associated Files:**
  - `src/main/java/com/premiumcalculator/model/PolicyType.java`
  - `src/main/java/com/premiumcalculator/service/PremiumCalculationService.java`
- **Evidence to Capture:** Source code of calculation service demonstrating formula:
  `Annual = Coverage * Rate` and `Total = Annual * Term`.

---

### Task 6: Data Transfer Objects & Validation Rules
- **Objective:** Create request/response DTOs with Jakarta Bean Validation constraints and centralized error models.
- **Associated Files:**
  - `src/main/java/com/premiumcalculator/dto/CalculationRequest.java`
  - `src/main/java/com/premiumcalculator/dto/CalculationResponse.java`
  - `src/main/java/com/premiumcalculator/dto/ErrorResponse.java`
- **Evidence to Capture:** DTO validation annotations (`@NotBlank`, `@DecimalMin`, `@Size`, `@Min`, `@Max`).

---

### Task 7: REST API Endpoints & Centralized Exception Handling
- **Objective:** Implement REST controllers for calculations, transactions, status updates, receipts, and global advice handlers.
- **Associated Files:**
  - `src/main/java/com/premiumcalculator/controller/PremiumCalculatorController.java`
  - `src/main/java/com/premiumcalculator/exception/GlobalExceptionHandler.java`
- **Endpoints Implemented:**
  - `POST /api/calculations` (201 Created)
  - `GET /api/transactions` (200 OK)
  - `GET /api/transactions/{id}` (200 OK)
  - `PATCH /api/transactions/{id}/status` (200 OK)
  - `GET /api/transactions/{id}/receipt` (200 OK text/plain)
- **Evidence to Capture:** Postman or cURL terminal execution demonstrating JSON response and HTTP status codes.

---

### Task 8: In-Memory Transaction Persistence & Receipt Generation
- **Objective:** Create an abstract repository layer for transactions and a text receipt voucher generation service.
- **Associated Files:**
  - `src/main/java/com/premiumcalculator/repository/TransactionRepository.java`
  - `src/main/java/com/premiumcalculator/repository/InMemoryTransactionRepository.java`
  - `src/main/java/com/premiumcalculator/service/TransactionService.java`
- **Evidence to Capture:** Downloaded text receipt file (`receipt-TXN-XXXXXXXX.txt`) showing formatted voucher.

---

### Task 9: Frontend Web Interface Development
- **Objective:** Develop responsive, accessible single-page web UI connected to real backend REST endpoints.
- **Associated Files:**
  - `src/main/resources/static/index.html`
  - `src/main/resources/static/css/style.css`
  - `src/main/resources/static/js/app.js`
- **Evidence to Capture:**
  - Full browser screenshot of the Premium Calculator home view (`http://localhost:8080`).
  - Active calculation summary cards and transaction table with status badges.

---

### Task 10: Automated Unit & Integration Testing (JUnit 5 & MockMvc)
- **Objective:** Implement comprehensive automated unit and integration tests verifying calculations, validation, and REST APIs.
- **Associated Files:**
  - `src/test/java/com/premiumcalculator/service/PremiumCalculationServiceTest.java`
  - `src/test/java/com/premiumcalculator/service/TransactionServiceTest.java`
  - `src/test/java/com/premiumcalculator/controller/PremiumCalculatorControllerIntegrationTest.java`
- **Commands Executed:**
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"
  .\mvnw.cmd test
  ```
- **Evidence to Capture:**
  - Surefire test report summary: **Tests run: 33, Failures: 0, Errors: 0, Skipped: 0**.
  - Maven `BUILD SUCCESS` banner.

---

### Task 11: End-to-End Automated Browser Testing (Selenium WebDriver)
- **Objective:** Implement automated E2E browser tests simulating user form submission and validation in a separate profile.
- **Associated Files:**
  - `src/test/java/com/premiumcalculator/e2e/PremiumCalculatorSeleniumTest.java`
  - `pom.xml` (Surefire exclusion and `selenium-tests` profile)
- **Commands Executed:**
  ```powershell
  .\mvnw.cmd test -Pselenium-tests
  ```
- **Evidence to Capture:** Terminal test execution logs showing browser driver initialization and assertion pass.

---

### Task 12: Production Multi-Stage Docker Containerization
- **Objective:** Build a multi-stage `Dockerfile` with Eclipse Temurin 17 JRE, non-root security context, and health check.
- **Associated Files:** `Dockerfile`, `.dockerignore`, `docker-compose.yml`
- **Commands Executed:**
  ```powershell
  docker build -t premium-calculator:latest .
  docker run -d --name premium-calculator-app -p 8080:8080 premium-calculator:latest
  docker ps
  ```
- **Evidence to Capture:**
  - Docker images list showing image size optimization.
  - `docker ps` displaying container status as `Up (healthy)`.

---

### Task 13: Jenkins CI/CD Declarative Pipeline Automation
- **Objective:** Construct an 8-stage declarative `Jenkinsfile` compatible with Windows `bat` and Linux `sh` agents.
- **Associated Files:** `Jenkinsfile`
- **Pipeline Stages:**
  1. Checkout
  2. Verify Java & Maven
  3. Run Tests
  4. Package Application
  5. Archive Artifact
  6. Build Docker Image
  7. Deploy Container
  8. Verify Health Check
- **Evidence to Capture:** Jenkins Pipeline stage view showing green checkmarks across all 8 stages.

---

### Task 14: Ansible Configuration Management & Deployment Orchestration
- **Objective:** Automate container provisioning, lifecycle management, and Actuator health check verification using Ansible.
- **Associated Files:**
  - `ansible/inventory.ini`
  - `ansible/playbook.yml`
  - `ansible/vars/main.yml`
  - `ansible/requirements.yml`
  - `ansible/README.md`
- **Commands Executed (in WSL/Linux):**
  ```bash
  ansible-playbook -i inventory.ini playbook.yml --syntax-check
  ansible-playbook -i inventory.ini playbook.yml
  ```
- **Evidence to Capture:** Ansible task execution output with `ok`, `changed`, and `failed=0`.

---

### Task 15: Monitoring, Health Checks & Operational Verification
- **Objective:** Monitor application runtime health using Spring Boot Actuator and verify deployment stability.
- **Associated Files:** `src/main/resources/application.properties`, `docs/COLLEGE_PRACTICAL_REPORT.md`
- **Endpoints Checked:**
  - `http://localhost:8080/actuator/health` (HTTP 200, `{"status":"UP"}`)
- **Evidence to Capture:**
  - JSON output from `/actuator/health`.
  - Frontend live heartbeat indicator badge showing `Backend: UP`.
