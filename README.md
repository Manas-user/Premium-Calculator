# Premium Calculator

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)]()
[![Tests](https://img.shields.io/badge/Tests-33%20Passed-blue.svg)]()
[![Docker](https://img.shields.io/badge/Docker-Multi--stage-2496ED.svg)](https://www.docker.com/)
[![Ansible](https://img.shields.io/badge/Ansible-Automated-EE0000.svg)](https://www.ansible.com/)

> **A full-stack, enterprise-grade insurance premium calculation and DevOps lifecycle project for college practical examination.**

---

## Student Identity & Practical Header
- **Student Name:** Manas Chand
- **Roll Number:** 23102C0030
- **College Practical Project:** DevOps & Software Engineering Laboratory
- **Target OS:** Windows 10/11 (64-bit)

---

## ⚠️ Academic Demonstration Disclaimer
**This application is an educational demonstration system.**
All policy categories and rates (Life: 1.20%, Health: 2.40%, Vehicle: 3.10%, Travel: 1.60%) are illustrative pedagogical estimates. They do **not** represent real insurance quotes, binding financial commitments, or underwriting evaluations.

---

## Table of Contents
1. [Overview & Features](#1-overview--features)
2. [Formula & Policy Rates](#2-formula--policy-rates)
3. [Technology Stack](#3-technology-stack)
4. [Project Directory Structure](#4-project-directory-structure)
5. [Prerequisites on Windows 10/11](#5-prerequisites-on-windows-1011)
6. [Running Locally on Windows](#6-running-locally-on-windows)
7. [Automated Testing](#7-automated-testing)
8. [Docker Containerization](#8-docker-containerization)
9. [Jenkins CI/CD Pipeline](#9-jenkins-cicd-pipeline)
10. [Ansible Deployment Orchestration](#10-ansible-deployment-orchestration)
11. [Git & GitHub Setup Guide](#11-git--github-setup-guide)
12. [REST API Documentation](#12-rest-api-documentation)
13. [College Practical Documents](#13-college-practical-documents)

---

## 1. Overview & Features

Premium Calculator allows customers and claims operators to calculate, evaluate, and track insurance premium estimates:
- **Interactive Calculator**: User enters customer name, chooses policy type, selects coverage amount (with quick-select buttons), and picks policy term (1–50 years).
- **Calculation Summary**: Instant display of annual rate, annual estimated premium, and total estimated premium.
- **Transaction History**: Real-time listing of transactions ordered latest first.
- **State Machine Transitions**: Transactions start in `SUBMITTED` state and can be `APPROVED` or `REJECTED`. Guard rules prevent illegal transitions from finalized states.
- **Receipt Voucher Download**: Generates and downloads an official text receipt (`receipt-TXN-XXXXXXXX.txt`).
- **Operational Health Check**: Live Actuator status indicator (`/actuator/health`) wired to the UI header.
- **Validation on Frontend & Backend**: Comprehensive constraints ensuring clean data entry.

---

## 2. Formula & Policy Rates

### Transparent Calculation Formula:
$$\text{Annual Premium} = \text{Coverage Amount} \times \text{Policy Rate}$$
$$\text{Estimated Total Premium} = \text{Annual Premium} \times \text{Policy Term (Years)}$$

### Policy Schedule:
| Policy Type | Illustrative Annual Rate | Minimum Coverage | Maximum Coverage | Term Range |
| :--- | :--- | :--- | :--- | :--- |
| **Life** | **1.20%** (`0.0120`) | ₹1,000 | ₹10,000,000 | 1 – 50 Years |
| **Health** | **2.40%** (`0.0240`) | ₹1,000 | ₹10,000,000 | 1 – 50 Years |
| **Vehicle** | **3.10%** (`0.0310`) | ₹1,000 | ₹10,000,000 | 1 – 50 Years |
| **Travel** | **1.60%** (`0.0160`) | ₹1,000 | ₹10,000,000 | 1 – 50 Years |

Calculations are computed using `java.math.BigDecimal` with `RoundingMode.HALF_UP` to prevent decimal floating-point distortion.

---

## 3. Technology Stack

- **Backend:** Java 17, Spring Boot 3.2.5, Spring Boot Actuator, Jakarta Bean Validation
- **Frontend:** HTML5, Modern CSS3 (Flexbox/Grid), Vanilla JavaScript (ES6+)
- **Testing:** JUnit 5, Mockito, Spring Test (MockMvc), Selenium WebDriver 4.18
- **Containerization:** Docker (Multi-stage Temurin 17 JRE, non-root user), Docker Compose
- **CI/CD:** Jenkins Declarative Pipeline (Windows `bat` & Linux `sh` stages)
- **Configuration Management:** Ansible Core (community.docker collection)
- **Version Control:** Git, GitHub (`develop` workflow)

---

## 4. Project Directory Structure

```
premium-calculator/
├── .github/
│   └── ISSUE_TEMPLATE/
│       ├── bug_report.md
│       └── feature_request.md
├── .mvn/
│   └── wrapper/
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── ansible/
│   ├── inventory.ini
│   ├── playbook.yml
│   ├── requirements.yml
│   ├── vars/
│   │   └── main.yml
│   └── README.md
├── docs/
│   ├── ARCHITECTURE.md
│   ├── BACKLOG.md
│   ├── COLLEGE_PRACTICAL_REPORT.md
│   └── PRACTICAL_TASKS_1_TO_15.md
├── src/
│   ├── main/
│   │   ├── java/com/premiumcalculator/
│   │   │   ├── controller/
│   │   │   │   └── PremiumCalculatorController.java
│   │   │   ├── dto/
│   │   │   │   ├── CalculationRequest.java
│   │   │   │   ├── CalculationResponse.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   └── StatusUpdateRequest.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InvalidInputException.java
│   │   │   │   ├── InvalidStatusTransitionException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── PolicyType.java
│   │   │   │   ├── PremiumTransaction.java
│   │   │   │   └── TransactionStatus.java
│   │   │   ├── repository/
│   │   │   │   ├── InMemoryTransactionRepository.java
│   │   │   │   └── TransactionRepository.java
│   │   │   ├── service/
│   │   │   │   ├── PremiumCalculationService.java
│   │   │   │   └── TransactionService.java
│   │   │   └── PremiumCalculatorApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── css/
│   │           │   └── style.css
│   │           ├── js/
│   │           │   └── app.js
│   │           └── index.html
│   └── test/
│       └── java/com/premiumcalculator/
│           ├── controller/
│           │   └── PremiumCalculatorControllerIntegrationTest.java
│           ├── e2e/
│           │   └── PremiumCalculatorSeleniumTest.java
│           └── service/
│               ├── PremiumCalculationServiceTest.java
│               └── TransactionServiceTest.java
├── .dockerignore
├── .gitignore
├── CONTRIBUTING.md
├── docker-compose.yml
├── Dockerfile
├── Jenkinsfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## 5. Prerequisites on Windows 10/11

Ensure the following tools are installed:
1. **Java JDK 17 (or 21 LTS)**:
   - Check version: `java -version`
   - Set environment variable if needed:
     ```powershell
     $env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"
     ```
2. **Git for Windows**:
   - Check version: `git --version`
3. **Docker Desktop (Optional for local container testing)**:
   - Ensure WSL 2 backend is enabled.
4. **Maven Wrapper**:
   - Included directly in this repository (`mvnw.cmd`). No manual Maven installation required!

---

## 6. Running Locally on Windows

Open **Windows PowerShell** and navigate to the project directory:

```powershell
cd "C:\Users\manas\.gemini\antigravity\scratch\premium-calculator"

# 1. Set JAVA_HOME in current session (adjust path if different)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"

# 2. Run application via Spring Boot Maven plugin
.\mvnw.cmd spring-boot:run
```

Once started:
- **Application Web UI:** Open [http://localhost:8080](http://localhost:8080)
- **Actuator Health Endpoint:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

Alternatively, run the pre-built JAR:
```powershell
java -jar target/premium-calculator-1.0.0.jar
```

---

## 7. Automated Testing

All automated tests are pre-verified and fully functional:

```powershell
# Run Unit and Integration Tests (33 tests)
.\mvnw.cmd test

# Run Selenium End-to-End Tests (requires Chrome/ChromeDriver)
.\mvnw.cmd test -Pselenium-tests

# Package the application into target/premium-calculator-1.0.0.jar
.\mvnw.cmd package -DskipTests
```

### Verified Test Summary:
- `PremiumCalculationServiceTest`: 14 tests (Life, Health, Vehicle, Travel rates, boundaries, precision).
- `TransactionServiceTest`: 9 tests (Creation, lookup, approval, rejection, state guards, receipts).
- `PremiumCalculatorControllerIntegrationTest`: 10 tests (MockMvc REST contracts, validation errors, 404s, Actuator health).
- **Result:** `Tests run: 33, Failures: 0, Errors: 0, Skipped: 0` (`BUILD SUCCESS`).

---

## 8. Docker Containerization

The project uses a production-ready multi-stage `Dockerfile`:
- Stage 1 compiles the application using Eclipse Temurin JDK 17.
- Stage 2 packages only the JAR using Eclipse Temurin JRE 17 under non-root user `appuser` (UID 1001).

### Build & Run Commands (Windows PowerShell):
```powershell
# 1. Build Docker image
docker build -t premium-calculator:latest .

# 2. Run container in detached mode
docker run -d --name premium-calculator-app -p 8080:8080 premium-calculator:latest

# 3. Check container status & health
docker ps

# 4. View application container logs
docker logs -f premium-calculator-app

# 5. Stop and remove container
docker stop premium-calculator-app
docker rm premium-calculator-app
```

### Running with Docker Compose:
```powershell
docker compose up -d
docker compose ps
docker compose down
```

---

## 9. Jenkins CI/CD Pipeline

The project includes a robust declarative `Jenkinsfile` compatible with both Windows agents (`bat`) and Linux agents (`sh`):

### Pipeline Stages:
1. **Checkout**: Source code clone from Git.
2. **Verify Java & Maven**: Toolchain version check.
3. **Run Tests**: Executes `mvn clean test` and records Surefire test reports.
4. **Package Application**: Builds executable JAR.
5. **Archive Artifact**: Archives `target/premium-calculator-1.0.0.jar`.
6. **Build Docker Image**: Builds `premium-calculator:${BUILD_NUMBER}`.
7. **Deploy Container**: Restarts container with port binding `8080:8080` (runs on `main` or `develop`).
8. **Verify Health**: Polls `http://localhost:8080/actuator/health` to confirm deployment health.

### Jenkins Setup on Windows:
1. In Jenkins dashboard, click **New Item** -> select **Pipeline** -> Name: `premium-calculator-pipeline`.
2. Under **Pipeline Definition**, select **Pipeline script from SCM**.
3. Choose **Git**, specify your repository URL, and set script path to `Jenkinsfile`.
4. Click **Build Now** to execute the pipeline.

---

## 10. Ansible Deployment Orchestration

Ansible provides automated, idempotent deployment and health verification.

### Running Ansible via Windows WSL 2:
```bash
# 1. Open WSL
wsl

# 2. Navigate to ansible folder
cd /mnt/c/Users/manas/.gemini/antigravity/scratch/premium-calculator/ansible

# 3. Install required collections
ansible-galaxy collection install -r requirements.yml

# 4. Verify syntax
ansible-playbook -i inventory.ini playbook.yml --syntax-check

# 5. Execute playbook
ansible-playbook -i inventory.ini playbook.yml
```

---

## 11. Git & GitHub Setup Guide

Follow these commands to push the project to your new GitHub repository:

```powershell
cd "C:\Users\manas\.gemini\antigravity\scratch\premium-calculator"

# 1. Initialize Git repository
git init

# 2. Configure identity
git config user.name "Manas Chand"
git config user.email "manas.chand@college.edu"

# 3. Add all files and make initial commit
git add .
git commit -m "feat(init): complete Premium Calculator full-stack and DevOps project"

# 4. Create develop branch
git branch -M main
git branch develop

# 5. Link to your new GitHub repository
# (Create a new empty repository named 'premium-calculator' on GitHub first)
git remote add origin https://github.com/<your-github-username>/premium-calculator.git

# 6. Push main and develop branches
git push -u origin main
git push -u origin develop
```

---

## 12. REST API Documentation

### 1. Calculate & Record Transaction
- **Method:** `POST /api/calculations`
- **Request Body:**
  ```json
  {
    "customerName": "Rahul Sharma",
    "policyType": "LIFE",
    "coverageAmount": 100000.00,
    "policyTerm": 5
  }
  ```
- **Response (201 Created):**
  ```json
  {
    "id": "TXN-8F3A29B1",
    "customerName": "Rahul Sharma",
    "policyType": "LIFE",
    "policyDisplayName": "Life Insurance",
    "coverageAmount": 100000.00,
    "policyTerm": 5,
    "annualRate": 0.0120,
    "ratePercentage": "1.2%",
    "annualPremium": 1200.00,
    "totalPremium": 6000.00,
    "status": "SUBMITTED",
    "createdAt": "2026-10-22T21:40:00.000",
    "disclaimer": "Demonstration estimate only, not a real insurance quote. Illustrative educational rates."
  }
  ```

### 2. List All Transactions
- **Method:** `GET /api/transactions`
- **Response (200 OK):** Array of calculation response objects ordered newest first.

### 3. Retrieve Transaction by ID
- **Method:** `GET /api/transactions/{id}`
- **Response (200 OK):** Calculation response object.
- **Error (404 Not Found):** If ID does not exist.

### 4. Update Status (Approve / Reject)
- **Method:** `PATCH /api/transactions/{id}/status`
- **Request Body:**
  ```json
  {
    "status": "APPROVED",
    "notes": "Verified client documentation"
  }
  ```
- **Response (200 OK):** Updated transaction object with status `APPROVED`.

### 5. Download Text Receipt Voucher
- **Method:** `GET /api/transactions/{id}/receipt`
- **Response (200 OK):** Plain text file attachment (`receipt-TXN-XXXXXXXX.txt`).

### 6. Health Check Endpoint
- **Method:** `GET /actuator/health`
- **Response (200 OK):** `{"status":"UP"}`.

---

## 13. College Practical Documents

Detailed project documentation is available inside the `docs/` folder:
- **College Practical Report (20 Sections):** [`docs/COLLEGE_PRACTICAL_REPORT.md`](docs/COLLEGE_PRACTICAL_REPORT.md)
- **Practical Tasks 1–15 Proposed Mapping:** [`docs/PRACTICAL_TASKS_1_TO_15.md`](docs/PRACTICAL_TASKS_1_TO_15.md)
- **System Architecture & Technical Design:** [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)
- **Project Backlog:** [`docs/BACKLOG.md`](docs/BACKLOG.md)
- **Contribution & Git Workflow Guide:** [`CONTRIBUTING.md`](CONTRIBUTING.md)
