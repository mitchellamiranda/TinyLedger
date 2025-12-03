# TinyLedger

**Author:** Mitchell Miranda

## Overview

TinyLedger is a simple ledger API built with **Java 21**, **Maven**, **Spring 4.3** (XML Dependency Injection only), **JUnit 4**, and **Mockito 5**.  
It runs a lightweight HTTP server (JDK `HttpServer`) and supports:

- Create accounts
- Record **deposits** and **withdrawals**
- View current balance
- View transaction history

**Scope & assumptions:**
- In-memory persistence (`ConcurrentHashMap`)
- Per-account locks for basic concurrency (not full transactions)
- No authentication/authorization, logging, or atomic/ACID guarantees
- Focus on approach and trade-offs for a tiny take-home assignment

## Quick Start

```bash
mvn -q -DskipTests package
java -jar target/TinyLedger-1.0.0.jar
# Server listens on http://localhost:8080
