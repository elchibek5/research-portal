# Haritara Research Portal

A full-stack educational and research platform built with Spring Boot 4 and MySQL. This project serves as a "Walking Skeleton" to demonstrate a fully integrated database and backend API.

## 🚀 Tech Stack
* **Backend:** Java 21, Spring Boot 4.0.3
* **Database:** MySQL 8.x
* **Build Tool:** Maven

## 🛠️ Getting Started

### Prerequisites
* JDK 21
* MySQL Community Server
* IntelliJ IDEA

### Database Setup
1. Create a database named `haritara`.
2. Create a user `ht_dev` with the necessary privileges (see `docs/adr/001-initial-tech-stack.md` for rationale).

### Configuration
1. Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties`.
2. Update the `spring.datasource.password` with your local MySQL password.

### Running the App
Run `ResearchPortalApplication.java` from IntelliJ or use Maven:
```bash
mvn spring-boot:run
```

## 📜 Documentation
**Architecture decisions are tracked in the docs/adr/ folder.**