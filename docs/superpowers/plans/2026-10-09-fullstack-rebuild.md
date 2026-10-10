# Library Management Fullstack Rebuild Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn the existing MyBatis REST API into a single-repository React and Java application with reproducible Ubuntu WSL deployment.

**Architecture:** Preserve the existing Java backend and move it under `backend/`. Add small collection endpoints needed by the UI. React calls relative `/api` paths; Vite proxies them in development and Nginx does so in production. MySQL remains the source of truth.

**Tech Stack:** Java 21, Spring Boot 4.1, MyBatis, MySQL 8, React, TypeScript, Vite, Nginx, systemd.

## Global Constraints

- Keep the existing Git history and API routes where possible.
- Keep credentials out of Git.
- Use `backend/`, `frontend/`, `deploy/`, and `docs/` in one repository.
- Ship an executable fresh database schema that matches MyBatis SQL.
- Verify unit tests, builds, and an end-to-end borrow/return path when a database is available.

---

### Task 1: Preserve and organize backend

**Files:** Move `src/`, `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/` into `backend/`; update `.gitignore`, `README.md`, and `docs/superpowers/specs/2026-10-08-library-management-rebuild-design.md`.

**Interfaces:** Existing `/api/books`, `/api/readers`, `/api/borrow-records` remain stable.

- [ ] Check Git status and establish a feature branch in the current checkout.
- [ ] Run baseline tests with `./mvnw.cmd test` from the old root; record any environment-only failures.
- [ ] Move backend files and run `./mvnw.cmd test` from `backend/` to detect path regressions.
- [ ] Commit the organized backend after verification.

### Task 2: Collection endpoints for the UI

**Files:** `backend/src/test/java/com/pp/library/{reader,borrow}/controller/*Test.java`, service tests, mapper tests; corresponding production controller, service, mapper, XML files; `docs/API.md`.

**Interfaces:** `GET /api/readers` returns active readers; `GET /api/borrow-records` returns recent records. Existing routes remain stable.

- [ ] Add failing controller and service tests for both collection routes and run the targeted tests to confirm red.
- [ ] Add mapper methods and SQL that filter deleted readers and order borrow records newest first.
- [ ] Implement service and controller methods; run targeted tests and then full backend tests.
- [ ] Update API documentation and commit.

### Task 3: Correct fresh database setup

**Files:** `db/schema.sql`, optional schema verification script or mapper integration test, `docs/deployment.md`.

**Interfaces:** Tables `book`, `reader`, `borrow_record` with `borrowed_at`, `returned_at`, `status` columns matching existing mapper XML.

- [ ] Compare every mapped column with schema and write a failing fresh-schema check or integration test.
- [ ] Correct SQL syntax, keys and indexes; run it against disposable MySQL when available.
- [ ] Update setup instructions and commit.

### Task 4: React application

**Files:** `frontend/package.json`, `frontend/index.html`, `frontend/src/{App.tsx,main.tsx,api.ts,types.ts,styles.css}`, relevant tests, `frontend/vite.config.ts`.

**Interfaces:** React calls relative `/api` URLs and consumes existing JSON shapes. Three views: books, readers, circulation.

- [ ] Define palette, type scale and layout for a librarian's desk; document the compact design direction in the frontend README.
- [ ] Write failing tests for API error handling and a borrow/return user path; run to confirm red.
- [ ] Implement the typed API client, view switching, forms, tables and loading/error states.
- [ ] Run frontend tests and `npm run build`, review visual layout at desktop and mobile widths, then commit.

### Task 5: Ubuntu WSL deployment and handoff

**Files:** `deploy/nginx/library.conf`, `deploy/systemd/library-api.service`, `deploy/scripts/deploy.sh`, `docs/deployment.md`, root `README.md`, `.gitignore`.

**Interfaces:** Nginx serves `frontend/dist` and forwards `/api` to localhost:8080; service loads credentials from an Ubuntu environment file.

- [ ] Add deployment configs, explicit first-time database steps, and update procedure.
- [ ] Check Nginx and shell syntax where tools are available; verify no secrets or generated artifacts are tracked.
- [ ] Run backend tests, frontend tests/build, and Git diff checks. Document any cloud-computer checks that require the user's machine or network.
- [ ] Commit the deployment handoff and report exact verified behavior and remaining cloud steps.
