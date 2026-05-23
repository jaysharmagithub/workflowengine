# ⚙️ Workflow Engine — Multi-Step Approval System

[![CI Pipeline](https://github.com/jaysharmagithub/workflowengine/actions/workflows/ci.yml/badge.svg)](https://github.com/jaysharmagithub/workflowengine/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Coverage](https://img.shields.io/badge/coverage-90%25-success?logo=jacoco)](https://github.com/jaysharmagithub/workflowengine)
[![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)](./Dockerfile)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A production-ready **RESTful Workflow Engine** built with Spring Boot 3 that orchestrates configurable multi-step approval pipelines. Designed with clean architecture, JWT-based security, and full CI/CD automation.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [API Reference](#-api-reference)
- [How Approval Flow Works](#-how-the-approval-flow-works)
- [Getting Started](#-getting-started)
- [Running with Docker](#-running-with-docker)
- [Testing & Coverage](#-testing--coverage)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Project Structure](#-project-structure)

---

## 🔍 Overview

The Workflow Engine is a backend system that automates **multi-step approval processes** for organizational workflows such as leave applications and expense claims. Requests move through ordered approval steps, each gated by role-based authorization. Every action is recorded in a complete audit trail.

**Use cases it solves:**
- Leave approval pipelines (e.g., LEAVE → Approver → Admin)
- Expense claim routing with sequential sign-off
- Any domain requiring ordered, role-gated approvals

---

## ✨ Features

| Feature | Details |
|---|---|
| 🔐 **JWT Authentication** | Stateless auth using JJWT 0.12.5 with BCrypt password hashing |
| 🛡️ **Role-Based Access Control** | `REQUESTER`, `APPROVER`, `ADMIN` roles with method-level `@PreAuthorize` |
| 🔄 **Multi-Step Approval Engine** | Configurable ordered steps per request type; engine auto-advances or finalizes |
| 🚦 **Request Lifecycle** | Full `PENDING → APPROVED / REJECTED` state management |
| 📋 **Audit History** | Immutable per-request approval history with actor, action, and timestamp |
| ✅ **90%+ Test Coverage** | Enforced by JaCoCo; build fails below threshold |
| 🐳 **Dockerized** | Multi-stage Docker build with Eclipse Temurin JRE 17 Alpine image |
| 🤖 **CI/CD** | GitHub Actions pipeline with Maven cache, JaCoCo report upload, and SonarCloud scan |
| 🏗️ **JPA Auditing** | `BaseEntity` with `createdBy` / `createdAt` auto-populated via `AuditorAware` |

---

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Client (HTTP)                              │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                   ┌────────────▼────────────┐
                   │    JWT Auth Filter       │  ← validates Bearer token
                   └────────────┬────────────┘
                                │
          ┌─────────────────────▼──────────────────────┐
          │               REST Controllers              │
          │   AuthController         RequestController  │
          └─────────────────────┬──────────────────────┘
                                │
          ┌─────────────────────▼──────────────────────┐
          │               Service Layer                 │
          │         RequestService  |  UserService      │
          └──────┬──────────────────────────┬───────────┘
                 │                          │
   ┌─────────────▼──────┐      ┌────────────▼────────────┐
   │  Approval Engine   │      │     Auth / User Mgmt    │
   │  ─ Step lookup     │      │     ─ Register / Login  │
   │  ─ Role validation │      │     ─ Role assignment   │
   │  ─ History record  │      └─────────────────────────┘
   │  ─ State advance   │
   └─────────────┬──────┘
                 │
   ┌─────────────▼──────────────────────────────────────┐
   │                 JPA Repositories (H2 / any RDBMS)  │
   │  RequestRepo | ApprovalStepRepo | HistoryRepo | ... │
   └────────────────────────────────────────────────────┘
```

### Domain Model

```
User ─── ManyToMany ──► Role
Request ─── OneToMany ──► ApprovalHistory
ApprovalStep (type + stepOrder + role)   ← workflow config table
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security 6 + JJWT 0.12.5 |
| Persistence | Spring Data JPA + H2 (dev) |
| Validation | Spring Validation (Bean Validation 3) |
| Build | Maven + Maven Wrapper |
| Testing | JUnit 5 + Spring Security Test + Testcontainers |
| Coverage | JaCoCo (≥ 90% line coverage enforced) |
| Code Quality | SonarCloud + Qodana |
| Containerization | Docker (multi-stage, Eclipse Temurin 17 Alpine) |
| CI/CD | GitHub Actions |

---

## 📡 API Reference

### Auth Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/auth/register-user` | Public | Register a new user with roles |
| `POST` | `/auth/login` | Public | Authenticate and receive a JWT |

**Login response:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "token": "<JWT>",
  "roles": ["ROLE_REQUESTER"]
}
```

---

### Request / Workflow Endpoints

> All endpoints below require a valid `Authorization: Bearer <token>` header.

| Method | Endpoint | Role Required | Description |
|---|---|---|---|
| `POST` | `/requests` | `REQUESTER` | Create a new workflow request |
| `GET` | `/requests/{id}` | `REQUESTER`, `APPROVER`, `ADMIN` | Fetch request details |
| `POST` | `/requests/{id}/approve?userId=&role=` | `APPROVER`, `ADMIN` | Approve current step |
| `POST` | `/requests/{id}/reject?userId=&role=` | `APPROVER`, `ADMIN` | Reject the request |
| `GET` | `/requests/history/{id}` | `REQUESTER`, `ADMIN` | Get full approval audit trail |

**Create request body:**
```json
{
  "type": "LEAVE",
  "username": "john.doe"
}
```

---

## 🔄 How the Approval Flow Works

```
1. REQUESTER creates a request  →  status: PENDING, currentStepOrder: 1

2. APPROVER (step 1) calls /approve
       └── Engine checks: role matches ApprovalStep for (type, stepOrder=1)?
           ├── ✅ YES → records history, advances to stepOrder: 2
           └── ❌ NO  → throws "Unauthorized for this step"

3. ADMIN (step 2) calls /approve
       └── Engine checks: any next step exists?
           ├── YES → advance step order
           └── NO  → status: APPROVED  ✅

4. Any approver calls /reject at any step → status: REJECTED ❌

5. GET /requests/history/{id} → full ordered audit trail
```

The `approval_steps` table is the configuration table — you define the workflow by inserting rows:

| request_type | step_order | role |
|---|---|---|
| LEAVE | 1 | APPROVER |
| LEAVE | 2 | ADMIN |
| EXPENSE | 1 | APPROVER |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+ (or use `./mvnw`)

### Clone & Run

```bash
git clone https://github.com/jaysharmagithub/workflowengine.git
cd workflowengine

# Run the application
./mvnw spring-boot:run
```

The app starts on **`http://localhost:8080`** by default (configurable via `PORT` env var).

### Quick Test with cURL

```bash
# 1. Register a user
curl -X POST http://localhost:8080/auth/register-user \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jay","lastName":"Sharma","email":"jay@example.com","password":"pass123","roles":[{"name":"ROLE_REQUESTER"}]}'

# 2. Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jay@example.com","password":"pass123"}'

# 3. Create a workflow request (use token from step 2)
curl -X POST http://localhost:8080/requests \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"type":"LEAVE","username":"jay@example.com"}'
```

---

## 🐳 Running with Docker

```bash
# Build the image
docker build -t workflowengine .

# Run the container
docker run -p 8080:8080 workflowengine
```

The multi-stage Dockerfile:
1. **Stage 1** — Builds the JAR using `maven:3.8.4-openjdk-17`
2. **Stage 2** — Runs with slim `eclipse-temurin:17-jre-alpine` for minimal image size

---

## 🧪 Testing & Coverage

```bash
# Run all tests with coverage report
./mvnw clean verify
```

- Coverage report generated at `target/site/jacoco/index.html`
- **Build fails if line coverage drops below 90%** (enforced via JaCoCo `<check>` rule)
- Excludes: `entity`, `dto`, `config` packages, and the main application class

---

## 🤖 CI/CD Pipeline

Every push to `main` and every Pull Request triggers:

```
┌──────────────┐    ┌──────────────────┐    ┌──────────────────────┐    ┌───────────────┐
│   Checkout   │───►│  Setup JDK 17    │───►│ mvn clean verify     │───►│ Upload JaCoCo │
│              │    │  (Temurin)       │    │ (Tests + Coverage)   │    │ Report        │
└──────────────┘    └──────────────────┘    └──────────────────────┘    └───────────────┘
                                                                                │
                                                                        ┌───────▼───────┐
                                                                        │  SonarCloud   │
                                                                        │  Scan         │
                                                                        └───────────────┘
```

Maven dependency cache is keyed to `pom.xml` hash for fast builds.

---

## 📁 Project Structure

```
workflowengine/
├── .github/
│   └── workflows/
│       ├── ci.yml                     # CI pipeline
│       └── qodana_code_quality.yml    # Static analysis
├── src/main/java/com/techpulseIt/workflowengine/
│   ├── config/                        # JPA Auditing config & AuditorAware
│   ├── controller/
│   │   ├── AuthController.java        # /auth endpoints
│   │   └── RequestController.java     # /requests endpoints
│   ├── dto/                           # Response DTOs
│   ├── entity/
│   │   ├── BaseEntity.java            # Audit fields (id, createdBy, createdAt)
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Request.java               # Workflow request
│   │   ├── ApprovalStep.java          # Workflow config (type + order + role)
│   │   └── ApprovalHistory.java       # Immutable audit trail
│   ├── exception/                     # Global exception handler + custom exceptions
│   ├── repository/                    # Spring Data JPA repositories
│   ├── request/                       # Request body POJOs
│   ├── response/                      # Unified API response wrappers
│   ├── security/
│   │   ├── SecurityConfig.java        # Spring Security filter chain
│   │   ├── CorsConfig.java
│   │   └── jwt/                       # JWT filter, entry point, utilities
│   └── service/
│       ├── RequestService.java        # Core approval engine logic
│       ├── UserServiceImpl.java
│       └── RoleServiceImpl.java
├── src/test/                          # Unit & integration tests
├── Dockerfile                         # Multi-stage Docker build
└── pom.xml
```

---

## 🔑 Key Design Decisions

- **Stateless security** — No HTTP sessions; every request is authenticated via JWT, making the service horizontally scalable.
- **Data-driven workflow** — The approval chain is defined in the `approval_steps` table, not in code, so new workflows can be added without redeployment.
- **Self-approval prevention** — The engine explicitly blocks a requester from approving their own request.
- **Fail-fast coverage gate** — JaCoCo enforces ≥ 90% line coverage at build time, preventing untested code from merging.
- **Clean layer separation** — Controllers handle HTTP concerns, Services own business logic, Repositories are pure data access.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">
  <sub>Built with ❤️ by <strong>Jay Sharma</strong></sub>
</div>
