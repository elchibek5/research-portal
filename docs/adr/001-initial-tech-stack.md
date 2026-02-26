# ADR 001: Initial Tech Stack for Haritara Portal

## Status
Accepted

## Context
We need a robust, scalable foundation for the Haritara educational/research platform that can handle ACID-compliant data transactions and provide a RESTful API.

## Decision
We have chosen the following stack:
* **Backend:** Spring Boot 4.0.3 (Java 21)
* **Database:** MySQL Community Server 8.x
* **Build Tool:** Maven

## Rationale
Spring Boot 4 was selected for its native support of Java 21 features and modern Jakarta EE standards, ensuring the project remains maintainable through 2026. MySQL was chosen for its reliability and widespread industry use, which is critical for maintaining research data integrity.

## Consequences
Using a full-scale RDBMS like MySQL requires more local setup than H2, but ensures the development environment mirrors the production environment closely from Day 1.