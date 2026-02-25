# Prerequisites

- Java 25
- Node.js & npm
- Docker & Docker Compose

# Quick Start

1. Start the database:
   ```bash
   docker-compose up -d
   ```

2. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```

3. Access the application:
   - App: http://localhost:8080
   - Adminer (DB UI): http://localhost:8079

# Tests
To check integrity of the application:
```bash
   ./gradlew clean test
   ```