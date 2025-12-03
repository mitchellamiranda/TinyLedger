
# TinyLedger – Developer Setup Guide

## Overview

TinyLedger is a lightweight ledger API built with **Java 21**, **Maven**, and **Spring 4.3** (XML-based Dependency Injection). It uses an in-memory store for demo purposes and exposes a RESTful API via Java’s built-in `HttpServer`.

***

## Prerequisites

*   **Java 21** (JDK 21)  
    Ensure `JAVA_HOME` is set to your JDK 21 installation.
*   **Maven 3.8+**
*   (Optional) **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java support

***

## 1. Clone the Repository

```bash
git clone https://github.com/mitchellamiranda/tinyledger.git
cd tinyledger
```

***

## 2. Configure Environment

*   Make sure `JAVA_HOME` points to Java 21 installation:
    *   **Windows:**  
        Set via System Properties or:
        ```cmd
        setx JAVA_HOME "C:\Program Files\Java\jdk-21"
        ```
    *   **Linux/macOS:**  
        Add to your `.bashrc` or `.zshrc`:
        ```bash
        export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
        export PATH=$JAVA_HOME/bin:$PATH
        ```

***

## 3. Build the Project

```bash
mvn -q -DskipTests package
```

*   This will generate a runnable JAR in the `target/` directory, e.g., `TinyLedger-1.0.0-shaded.jar`.

***

## 4. Run the Application

```bash
java -jar target/TinyLedger-1.0.0-shaded.jar
```

*   The server will start on <http://localhost:8080>.

***

## 5. API Usage

*   See APIReference.md for full endpoint documentation and example `curl` commands.
*   Example: Create an account
    ```bash
    curl -X POST http://localhost:8080/api/v1/accounts \
      -H "Content-Type: application/json" \
      -d '{"name":"Alice", "currency":"EUR", "initialBalance":1000.00}'
    ```

***

## 6. Running Tests

```bash
mvn test
```

*   Runs all unit tests (JUnit 4, Mockito 5).

***

## 7. Common Issues

*   **JAVA\_HOME not set:**  
    Ensure your environment variables are correct and point to JDK 21.
*   **Port conflicts:**  
    If port 8080 is in use, change it in `application.yml`.

***

## 8. Project Structure

*   `src/main/java` – Source code
*   `src/test/java` – Unit tests
*   `applicationContext.xml` – Spring bean configuration
*   `application.yml` – Server config (port, etc.)
*   `APIReference.md` – API documentation

***

## 9. Contributing

*   Fork the repo, create a feature branch, and submit a pull request.
*   Follow code style and add/maintain unit tests for new features.

***
