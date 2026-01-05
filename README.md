# Product Catalog

Microservice to manage and distribute product master data.

## CI Status

[![Java CI with Maven](https://github.com/philipbolting/product-catalog/actions/workflows/maven.yml/badge.svg)](https://github.com/philipbolting/product-catalog/actions/workflows/maven.yml)
[![Dependabot Updates](https://github.com/philipbolting/product-catalog/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/philipbolting/product-catalog/actions/workflows/dependabot/dependabot-updates)

## Setup Instructions (development)

Prerequisites:
- Java 25
- Maven
- Docker

Create a .env file based on .env.example:
```
product-catalog$ cp .env.example .env
```
Set a random string as POSTGRES_PASSWORD in the .env file. Docker sets the superuser password for PostgreSQL to the value of this environment variable.

Spring Boot automatically derives the database connection details from the docker compose file. No datasource configuration in application.properties necessary.

Start the application:
```
product-catalog$ ./mvnw spring-boot:run
```

## Tech Stack:
- Backend: Spring Boot, Spring MVC, Spring Data JPA
- Database: PostgreSQL
- Security: Spring Security
- Build Tool: Maven
- Testing: JUnit, Mockito