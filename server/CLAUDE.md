# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

GluCoCare is a Spring Boot application for glucose care management. The server is built with Spring Boot 3.5.3, Java 21, and uses JWT authentication with Spring Security.

## Development Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run the application
./gradlew bootRun

# Clean build
./gradlew clean build
```

## Architecture

### Project Structure
- **Package Structure**: Feature-based organization under `com.glucocare.server`
- **Features**: Located in `src/main/java/com/glucocare/server/feature/`
- **Security**: JWT-based authentication with custom filters
- **Database**: MySQL with JPA/Hibernate (H2 for testing)

### Key Components

#### Feature Structure (Domain-Driven Design)
Each feature follows a consistent structure:
- `application/` - Use cases and application services
- `domain/` - Domain entities and repositories
- `dto/` - Data transfer objects
- `presentation/` - Controllers and API endpoints

#### Security Architecture
- JWT authentication with custom `JwtAuthFilter`
- `SecurityConfig` with CORS configuration for frontend (localhost:3000, localhost:8080)
- `InvalidAuthEntryPoint` for unauthorized access handling
- BCrypt password encoding

#### Base Infrastructure
- `BaseEntity` in `shared/domain/` provides auditing fields
- Global exception handling via `GlobalExceptionHandler`
- Custom exception types in `exception/` package

### Database Configuration
- MySQL connection configured in `application.yml`
- JPA auditing enabled via `@EnableJpaAuditing`
- Hibernate DDL auto-update mode

### Current Features
- **Member Management**: Registration, login, name updates
- **Authentication**: JWT-based with role-based access (MEMBER role)

## Testing
- Uses JUnit 5 platform
- Spring Security Test support included
- Run individual tests: `./gradlew test --tests ClassName`