# ADR 002: TypeScript for Frontend Development

## Status
Accepted

## Context
We are building a research portal that will eventually handle complex data models for research studies and participant applications. We need to ensure data integrity between the Spring Boot backend and the React frontend.

## Decision
We will use **TypeScript** (via Vite's React - TS templete) for all frontend development.

* **Type Safety:** TypeScript allows us to define 'interfaces' that match our Java 'Entities', catching data mismatches during development rather than at runtime
* **Documentation:** Types serve as living documentation for the props and state within our components.
* **Professional Standards:** TypeScript is the industry standard for modern enterprise React application.

## Consequences
There is a slightly steeper learning curve and more initial boilerplate code, but this is offset by significantly reduced debugging time as the project grows.