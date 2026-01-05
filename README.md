# Product Catalog

Microservice to manage and distribute product master data.

## Setup Instructions (development)

Prerequisites:
- Java 25
- Maven
- Docker

Set required environment variables:

Create an .env file based on the .env.example file:
```
product-catalog$ cp .env.example .env
```
Set a random string as POSTGRES_PASSWORD in the .env file. This password is used by docker compose to configure the PostgreSQL-container.

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