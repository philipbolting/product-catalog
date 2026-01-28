# Product Catalog

Microservice to manage and distribute product master data.

## CI Status

[![Java CI with Maven](https://github.com/philipbolting/product-catalog/actions/workflows/maven.yml/badge.svg)](https://github.com/philipbolting/product-catalog/actions/workflows/maven.yml)
[![Dependabot Updates](https://github.com/philipbolting/product-catalog/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/philipbolting/product-catalog/actions/workflows/dependabot/dependabot-updates)

## Setup Instructions (development)

Prerequisites:
- [Java 25](https://openjdk.org/projects/jdk/25/)
- [Maven](https://maven.apache.org)
- [Docker](https://www.docker.com) (for [Testcontainers](https://java.testcontainers.org))

Local manual testing:
1. run TestProductCatalogApplication#main
2. use the example requests in HTTPRequests.http to manually test the API

## Tech Stack:
- Backend: [Spring Boot](https://spring.io/projects/spring-boot), Spring MVC, Spring Data JPA
- Database: [PostgreSQL](https://www.postgresql.org)
- Security: [Spring Security](https://spring.io/projects/spring-security)
- Build Tool: [Maven](https://maven.apache.org)
- Testing: [JUnit](https://junit.org), [Mockito](https://site.mockito.org), [Testcontainers](https://java.testcontainers.org)