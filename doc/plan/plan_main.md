# Community Water Level IoT System Development Plan v0.0.0

## 📋 Project Overview

**Project Name**: Community Water Level IoT Backend  
**Project Goal**: Build a high-performance, scalable water level monitoring backend service based on youlai-boot open source framework  
**Development Mode**: Open Source Refactoring + Agile Iteration, Stage-by-Stage Expansion  
**Base Framework**: youlai-boot (Spring Boot 3.5)  
**Document Version**: v0.0.0  
**Last Updated**: 2025-09-10

### 🏗️ Project Architecture Foundation

**Open Source Base**:

- Based on [youlai-boot](https://github.com/youlaitech/youlai-boot) open source project
- Complete Spring Boot 3.x infrastructure included
- Contains user management, permission control, caching and other basic functions

**Refactoring Strategy**:

- **Subtraction Strategy**: Prioritize removing unrelated business modules
- **Fix & Optimize**: Resolve existing issues and upgrade dependencies  
- **Feature Extension**: Add IoT-specific features on stable foundation, including user-friendly natural language queries (NL2SQL) and RAG (Retrieval Augmented Generation) intelligent analysis capabilities

**Technical Debt Management**:

- Fix known bugs and security issues
- Upgrade outdated dependency packages
- Unify code style and architecture

## 🗂️ Development Stage Planning

### 📊 Progress Overview

| Phase | Name | Status | Priority | Estimated Duration | Expansion Status |
|-------|------|--------|----------|-------------------|------------------|
| Phase 0 | Open Source Base Refactoring | 🔵 Pending | P0 | 1.5 weeks | 📝 [Click to Expand](#phase-0-open-source-base-refactoring) |
| Phase 1 | Project Architecture Optimization | ⚫ Not Planned | P0 | 1 week | 📁 Not Expanded |
| Phase 2 | Database & ORM Refactoring | ⚫ Not Planned | P0 | 1 week | 📁 Not Expanded |
| Phase 3 | MQTT + Sparkplug B Integration | ⚫ Not Planned | P0 | 2 weeks | 📁 Not Expanded |
| Phase 4 | Cache & Session Optimization | ⚫ Not Planned | P1 | 1 week | 📁 Not Expanded |
| Phase 5 | Security & Permission Control | ⚫ Not Planned | P0 | 1.5 weeks | 📁 Not Expanded |
| Phase 6 | RAG Intelligent Analysis System | ⚫ Not Planned | P2 | 3 weeks | 📁 Not Expanded |
| Phase 7 | Monitoring & Observability | ⚫ Not Planned | P1 | 1 week | 📁 Not Expanded |
| Phase 8 | Containerization & Deployment | ⚫ Not Planned | P1 | 1.5 weeks | 📁 Not Expanded |

**Status Legend**:

- 🔵 Pending | 🟡 In Progress | 🟢 Completed | 🔴 Blocked | ⚫ Not Planned

**Priority Explanation**:

- P0: Core functions, must complete
- P1: Important functions, recommended to complete
- P2: Value-added functions, complete if time permits

---

## Phase 0: Open Source Base Refactoring

> **Goal**: Refactor based on youlai-boot open source framework, remove unrelated functions and fix issues  
> **Status**: 🔵 Pending  
> **Estimated Duration**: 1.5 weeks  
> **Prerequisites**: youlai-boot source code analysis completed  
> **Deliverables**: Clean, runnable project foundation

### 📋 Task List

#### 0.1 Source Code Analysis & Assessment (Estimated 2 days)

- [ ] Analyze youlai-boot overall architecture
- [ ] Identify core modules vs non-core modules
- [ ] Assess existing function vs IoT requirements match
- [ ] Create removal function list

#### 0.2 Unrelated Function Removal (Estimated 3 days)

- [ ] Remove redundant user management functions
- [ ] Clean unnecessary business modules
- [ ] Remove excess Controllers and Services
- [ ] Simplify permission system to basic requirements

#### 0.3 Issue Fixing & Upgrades (Estimated 2 days)

- [ ] Fix known bugs and issues
- [ ] Upgrade dependency packages to latest stable versions
- [ ] Resolve security vulnerabilities
- [ ] Unify code style

#### 0.4 Basic Function Verification (Estimated 1 day)

- [ ] Verify application starts normally
- [ ] Test basic API functions
- [ ] Confirm database connection is normal
- [ ] Verify logging and monitoring functions

### 🎯 Stage Acceptance Criteria

#### Function Acceptance

- [ ] Application starts normally without errors
- [ ] Basic CRUD API functions work normally
- [ ] Database Schema cleanup completed
- [ ] Unrelated modules completely removed

#### Quality Acceptance

- [ ] Code coverage > 70% (for retained functions)
- [ ] No high-risk security issues
- [ ] Startup time < 30 seconds
- [ ] Memory usage reduced by > 30%

### 🚧 Risks & Prevention Measures

| Risk Item | Impact Level | Probability | Prevention Measures |
|-----------|-------------|-------------|-------------------|
| Complex function dependencies | High | Medium | Detailed module dependency analysis |
| Database Schema changes | Medium | Medium | Backup original database |
| Third-party package compatibility | Medium | Low | Gradual upgrade and testing |

### 📝 Detailed Implementation Notes

#### Function Removal List (Estimated)

```text
Modules to Remove:
- Complex user management functions (keep basic auth)
- Multi-tenant related functions
- Complex workflow engine
- Redundant reporting functions
- CMS content management
```

#### Core Functions to Retain

```text
Core Retention:
- Spring Boot 3.x basic architecture
- Spring Security basic security
- Spring Data JPA data persistence
- Redis caching mechanism
- Basic API architecture
```

---

## Phase 1: Project Architecture Optimization

> **Status**: ⚫ Not Planned  
> **Expansion Condition**: Expand detailed content after Phase 0 completion

**Overview Tasks**:

- Optimize project structure and naming
- Establish IoT-specific configuration
- Set up development environment
- Build testing framework

---

## Phase 2: Database & ORM Refactoring

> **Status**: ⚫ Not Planned  
> **Expansion Condition**: Expand detailed content after Phase 1 completion

**Overview Tasks**:

- Design IoT data models based on existing Schema
- Adjust Spring Data JPA configuration
- Design water level monitoring related entities
- Database migration and upgrades

---

## Phase 3: MQTT + Sparkplug B Integration

> **Status**: ⚫ Not Planned  
> **Expansion Condition**: Expand detailed content after Phase 2 completion

**Overview Tasks**:

- EMQX connection configuration integration
- Sparkplug B protocol implementation
- Message processing architecture establishment
- Community grouping mechanism design

---

## Phase 4: Cache & Session Optimization

> **Status**: ⚫ Not Planned  
> **Expansion Condition**: Expand detailed content after Phase 3 completion

**Overview Tasks**:

- Optimize existing Redis configuration
- IoT-specific caching strategies
- Distributed Session adjustments
- Performance monitoring and tuning

---

## Phase 5: Security & Permission Control

> **Status**: ⚫ Not Planned  
> **Expansion Condition**: Expand detailed content after Phase 4 completion

**Overview Tasks**:

- Spring Security 6 configuration
- JWT authentication mechanism
- Role permission design
- API security protection

---

## Phase 6: RAG Intelligent Analysis System

> **Status**: ⚫ Not Planned  
> **Expansion Condition**: Expand detailed content after Phase 5 completion

**Overview Tasks**:

- Qdrant vector database integration
- Knowledge base construction
- RAG query implementation
- NL2SQL (Natural Language to SQL) query engine
- AI assistance functions

---

## Phase 7: Monitoring & Observability

> **Status**: ⚫ Not Planned  
> **Expansion Condition**: Expand detailed content after Phase 6 completion

**Overview Tasks**:

- Prometheus metrics collection
- Grafana dashboard
- Distributed tracing
- Alert mechanisms

---

## Phase 8: Containerization & Deployment

> **Status**: ⚫ Not Planned  
> **Expansion Condition**: Expand detailed content after Phase 7 completion

**Overview Tasks**:

- Docker image construction
- Kubernetes deployment configuration
- Helm Charts preparation
- Production environment deployment

---

## 📈 Project Tracking

### Update History

| Date | Version | Update Content | Updated By |
|------|---------|----------------|------------|
| 2025-09-10 | v0.0.0 | Initialize project plan, add Phase 0 Open Source base refactoring stage | - |

### Next Update Plan

**Expected Update Time**: When Phase 0 starts  
**Expected Update Content**:

- Phase 0 progress tracking and youlai-boot analysis results
- Phase 1 detailed task expansion
- Function removal list confirmation
- Risk assessment updates

### Open Source Base Information

**youlai-boot Version**: Current version to be confirmed in Phase 0  
**License**: Apache License 2.0  
**GitHub**: [youlai-boot repository](https://github.com/youlaitech/youlai-boot)  
**Tech Stack**: Spring Boot 3.x, Spring Security 6, MySQL/PostgreSQL, Redis

---

## 📚 Document Management Guidelines

### Stage Expansion Principles

1. **Initial State**: All phases show only overview tasks
2. **Start Expansion**: Expand detailed content when preparing to execute a phase
3. **Progress Tracking**: Regularly update task completion status and progress
4. **Experience Summary**: Record implementation notes and experience after phase completion

### Document Version Management

- **v0.x.x**: Planning phase versions
- **v1.x.x**: Development phase versions
- **vx.y.z**: x=major version, y=phase completion, z=minor updates

### Usage Guide

- **Project Manager**: Track overall progress and resource allocation
- **Development Team**: View current stage detailed tasks
- **Stakeholders**: Understand project progress and milestones

---

## 🤖 Key Technical Features

### NL2SQL Natural Language Query Engine

**Feature Description**:

- Allow users to query water level data using natural language
- Automatically convert natural language to SQL queries
- Support complex queries across multiple communities and time ranges

**Implementation Approach**:

- Integration with LLM (Large Language Model) for natural language understanding
- Database schema awareness for accurate SQL generation
- Query validation and security filtering
- Result formatting and visualization

**Example Use Cases**:

```text
User Input: "Show me the water level trend for Community A in the last 7 days"
Generated SQL: SELECT timestamp, water_level FROM sensor_data 
               WHERE community_id = 'A' AND timestamp >= NOW() - INTERVAL '7 days'
               ORDER BY timestamp;

User Input: "Which communities had water level alerts this month?"
Generated SQL: SELECT DISTINCT c.name FROM communities c 
               JOIN alerts a ON c.id = a.community_id 
               WHERE a.alert_type = 'water_level' AND a.created_at >= DATE_TRUNC('month', NOW());
```

**Technical Stack**:

- LLM API integration (OpenAI/Azure OpenAI/Local LLM)
- Schema-to-Text conversion for database understanding
- SQL parsing and validation libraries
- Caching layer for frequently asked queries
