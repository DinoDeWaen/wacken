# Project: Wacken Planner 2026

## 1. Purpose of this document
This `project.md` file defines:
- The **product vision**
- **Functional and non-functional requirements**
- **Architectural constraints**
- **Development methodology**
- **Quality standards**
- **CI/CD expectations**
- **How backlog.md user stories must be written and interpreted**

This document acts as:
- A **north star** for the project
- A **system prompt** for Codex / AI coding agents
- A **governance contract** for architecture, testing, and delivery

---

## 2. Product Vision
Build an Android application that helps a group of friends:
- Rate bands at **Wacken Open Air 2026**
- Automatically compute a **shared, conflict-aware festival schedule**
- Take into account:
  - Individual preferences
  - Group veto rules
  - Stage (podium) distances
  - Travel time feasibility
  - Lunch time constraints
- Output a **clear, printable timeline**

---

## 3. Target Platform & Delivery
### Platforms
- **Primary**: Android application
- **Secondary (optional for MVP)**: Web app (only if it significantly simplifies early delivery)

### Android specifics
- Language: **Java**
- Build system: **Gradle**
- Output:
  - Debug APK for local installation
  - (Optional later) Free distribution via Play Store / internal testing track

---

## 4. Non-Functional Requirements (MANDATORY)

### 4.1 Architecture
- **Domain-Driven Design (DDD)**
- **Clean Architecture**
- Strict separation of:
  - Domain
  - Application
  - Infrastructure
  - UI (Android)

**Forbidden:**
- Business logic in UI / Activities / Fragments
- Anemic domain models
- God services

---

### 4.2 Coding Standards
- Follow **Clean Code principles** (Robert C. Martin)
- Small, expressive classes and methods
- Explicit naming, no abbreviations
- No duplicated logic
- No comments explaining *what* the code does — code must explain itself

---

### 4.3 Development Methodology
- **Test Driven Development (TDD)** is mandatory
  - Write failing test first
  - Then production code
  - Then refactor
- Every feature starts with:
  - Domain tests
  - Then application tests
  - UI tests only when meaningful

---

### 4.4 Testing Requirements
#### Unit Tests
- Mandatory for:
  - Domain logic
  - Decision rules
  - Conflict resolution
  - Travel feasibility
- Frameworks:
  - JUnit 5
  - Mockito / Fake implementations

#### QA / Test Suite
- A dedicated **QA test suite** must exist
- Covers:
  - End-to-end scheduling scenarios
  - Conflict resolution
  - Edge cases (vetoes, lunch, travel infeasible)
- Can be:
  - JVM-based scenario tests
  - Or Android Instrumentation tests (later phase)

#### Coverage
- Target:
  - **≥80% domain coverage**
  - **≥70% application layer coverage**
- Coverage must be enforced in CI

---

## 5. CI / CD Requirements

### 5.1 Continuous Integration
A CI pipeline **must exist from MVP 1** and must:

1. Checkout code
2. Run:
   - Unit tests
   - QA test suite
3. Enforce:
   - Build failure on test failure
4. Build:
   - Android APK

Example tools (non-exclusive):
- GitHub Actions
- GitLab CI
- Jenkins

---

### 5.2 Continuous Delivery
- CI must:
  - Produce a downloadable APK artifact
  - Version it clearly (e.g. `wacken-planner-0.1.0-debug.apk`)
- Deployment target (MVP):
  - Artifact storage (CI artifacts)
- Later:
  - Play Store internal testing track

---

## 6. Functional Scope (Recap)

### Core Concepts
- Bands
- Performances (band + stage + time)
- Stages (podia)
- Distances between stages
- Users
- Groups of friends
- Ratings (0–4)

### Rating Scale
| Rating | Meaning |
|------:|--------|
| 0 | Veto |
| 1 | OK / indifferent |
| 2 | Like, fine to miss |
| 3 | Want to see |
| 4 | Must see |

---

## 7. Core Decision Rules (AUTHORITATIVE)

### 7.1 Single Band Decision
Given all ratings in a group:
- If **any 4** → GO
- If max = 3:
  - GO unless **2 or more vetoes**
- If max = 2:
  - GO unless **any veto**
  - During lunch (12–14): OPTIONAL
- If max = 1 → OPTIONAL
- If max = 0 → DO NOT GO

---

### 7.2 Conflict Resolution (Overlapping Performances)
1. Prefer any band with a **4**
2. If only 3s:
   - Choose band with most 3s
   - Tie-breaker: fewer vetoes
   - Tie-breaker: shortest travel distance
3. If only 2s:
   - OPTIONAL
   - Choose most 2s
4. If only 1s:
   - OPTIONAL
5. If all vetoed → none selected

---

### 7.3 Travel Feasibility
- A performance is invalid if:
previousEndTime + travelTime > nextStartTime

- If infeasible:
- Re-run conflict resolution excluding infeasible options
- When choosing between equal 3s:
- Prefer the one **closest to previous 4**

---

### 7.4 Lunch Window (12:00–14:00)
- Schedule must insert a **Lunch block**
- Show nearby food options:
- Close to previous stage
- Close to next stage

---

## 8. Schedule Output Requirements
- Timeline view per day
- For each slot:
- Selected band (BIG)
- Stage (BIG)
- Time range
- Travel time to next stage
- Lost alternatives (SMALL)
- Lunch clearly visualized

---

## 9. Admin Capabilities (MVP)
Admin UI or config allows:
- Upload CSVs:
- Bands
- Stages
- Performances
- Distances
- Food
- Validation:
- Missing references
- Overlaps
- Unknown stages

---

## 10. MVP Breakdown Strategy (MANDATORY)

### Rule
- Each MVP is split into:
- Features
- Each feature split into **small user stories**
- User stories must:
- Be independently testable
- Have clear acceptance criteria
- Be small enough to finish in 1–2 sessions

---

## 11. Backlog.md Contract

### User Story Template (MANDATORY)
Each story in `backlog.md` must follow this structure:


## US-XXX: <Short title>

**As a** <type of user>  
**I want** <capability>  
**So that** <business value>

### Acceptance Criteria
- Given ...
- When ...
- Then ...

### Notes
- Edge cases
- Test hints

## 12. MVP Roadmap (High Level)
### MVP 1 – Foundations

- Android project setup
- CI pipeline
- Domain model
- CSV import
- Band listing
- Rating bands
- Full unit test setup

### MVP 2 – Groups & Scheduling

Groups

- Friend invites (link-based)
- Decision engine
- Conflict resolution
- Timeline generation

### MVP 3 – Travel & Lunch

- Stage distances
- Travel feasibility
- Lunch logic
- Food suggestions

### MVP 4 – UX & Polish

- Printable schedule
- Visual hierarchy
- Performance optimization

## 13. Instructions for Codex / AI Agents

You are REQUIRED to:

Follow this document strictly

Implement DDD + Clean Architecture

Write tests before production code

Reject shortcuts

Split work according to backlog.md user stories

Never mix UI logic with domain logic

Prefer clarity over cleverness

If a requirement is unclear:

Ask for clarification

Do NOT invent rules

## 14. Definition of Done (GLOBAL)

A story is done only if:

Acceptance criteria pass

Unit tests exist and pass

Code follows clean architecture

CI is green

No architectural shortcuts were taken

