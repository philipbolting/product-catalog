# Product Catalog

Microservice to manage and distribute product master data.

## Setup Instructions (development)

Prerequisites:
- Java 25
- Maven
- Docker

Set required environment variables:
```
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=product_catalog
export POSTGRES_USER=product_catalog_admin
export POSTGRES_PASSWORD=$(pwgen -s 20 1)
export POSTGRES_JDBC_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}
```

Start the PostgreSQL database Docker container:
```
product-catalog$ docker compose up --detach
```

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